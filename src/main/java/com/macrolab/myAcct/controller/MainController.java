package com.macrolab.myAcct.controller;

import com.macrolab.myAcct.Main;
import com.macrolab.myAcct.common.CommUI;
import com.macrolab.myAcct.model.DBFile;
import com.macrolab.myAcct.model.TMyAcct;
import com.macrolab.myAcct.service.DBService;
import com.macrolab.myAcct.service.MyAcctService;
import com.macrolab.myAcct.util.DateUtil;
import com.macrolab.myAcct.util.ToolUtil;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Paint;
import javafx.scene.web.HTMLEditor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class MainController implements Initializable {
    Logger logger = LoggerFactory.getLogger(DBService.class);

    @FXML
    public Button btnCreateDB;
    @FXML
    private Button btnBackupDB;
    @FXML
    public Label labelAppStatus;
    @FXML
    public HTMLEditor htmlEditor;
    @FXML
    public Label labelId;
    @FXML
    public Slider slider;
    @FXML
    public ChoiceBox<DBFile> choiceBoxDBFile;
    @FXML
    private Button btnSearch;
    @FXML
    private TextField txtSearch;
    @FXML
    private ListView<TMyAcct> listAcct;
    @FXML
    private TextField txtAcctName;
    @FXML
    private Label labelKeyVerifyCode;
    @FXML
    private PasswordField txtKey;
    @FXML
    private Button btnKeyVerifyCode;
    @FXML
    private Button btnNew;
    @FXML
    private Button btnSave;
    @FXML
    private Button btnDelete;
    @FXML
    private Label labelUpdateDate;
    @FXML
    private Label labelCreateDate;
    @FXML
    private Label labelMAC;
    @FXML
    private Label labelPath;
    @FXML
    private Label labelContentStatus;

    // 当前编辑的数据
    TMyAcct myAcct;

    // list控件对应的数据
    List<TMyAcct> myAcctList;

    // choiceBox控件对应的数据
    List<DBFile> listDBFile;

    // list中上次选中的记录
    private int lastListFocusId = -1;

    // 当前内容是否有变更
    boolean isContentChanged = false;

    // 当创建一个新记录时设置为true，用于更新首次编写的内容
    boolean hasNewMyAcct = false;

    // 初始化标志
    boolean initialize = true;

    private MyAcctService myAcctService;

    private Main application;

    public void setApp(Main application) {
        this.application = application;
    }

    /**
     * 界面初始化
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        myAcctService = new MyAcctService();
        clearUI();
        timerSave();  // 启动自动保存线程
        choiceBoxDBFile.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                DBFile dbFile = listDBFile.get((Integer) newValue);
                if (ToolUtil.isNotEmpty(dbFile)) {
                    logger.info("切换到【" + dbFile.getName() + "】资料库");
                    myAcctService.setDbService(dbFile);
                    loadList();
                }
            }
        });

//        slider.valueProperty().addListener(new ChangeListener<Number>() {
//            public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {
//                logger.debug("资料【" + myAcct.getId() + "】优先级调整：" + Math.round(new_val.doubleValue()));
//                isContentChanged = true;
//                labelContentStatus.setText("资料优先级调整为：" + Math.round(new_val.doubleValue()));
//                labelContentStatus.setTextFill(Paint.valueOf("RED"));
//            }
//        });


        initialize = false;  // 初始化结束
    }

    /**
     * 初始化界面控件状态
     */
    private void clearUI() {
        myAcct = new TMyAcct();
        changeContent();
        labelId.setText("资料编号");
        labelContentStatus.setText("");
        htmlEditor.setDisable(true);
        txtKey.setText("");
        btnNew.setDisable(true);
        btnSave.setDisable(true);
        slider.setValue(0);
        slider.setDisable(true);
        lastListFocusId = -1;

        // slider优先级 tooltip
        final Tooltip tooltip = new Tooltip();
        tooltip.setText("资料优先级，数值高，优先级高");
        slider.setTooltip(tooltip);
//
//        Image image = null;
//        try {
//            image = new Image(new FileInputStream(Main.workpath+"\\src\\main\\resources\\images/warn.png"));
//            tooltip.setGraphic(new ImageView(image));
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
    }

    /**
     * 初始化读取数据，加载到页面
     */
    public void loadList() {
        String keyVerifyCode = myAcctService.keyVerifyCode(txtKey.getText());
        myAcctList = myAcctService.queryMyAcct(txtSearch.getText(), keyVerifyCode);
        ObservableList<TMyAcct> items = FXCollections.observableArrayList(myAcctList);
        listAcct.setItems(items);
    }

    // 缺省加在第一个库文件
    public void choiceDefaultDBFile() {
        choiceBoxDBFile.getSelectionModel().clearAndSelect(0);
    }

    /**
     * 初始化，加载数据目录下的sqliteDB文件
     */
    public void loadDBFile() {
        String path = Main.workpath;
        labelAppStatus.setText("  工作路径：" + path);
        listDBFile = myAcctService.getDBFilelist(path);
        if (listDBFile.size() == 0) {
            CommUI.errorBox("未找到数据资料库", "工作路径：" + path);
        }
        ObservableList<DBFile> items = FXCollections.observableArrayList(listDBFile);
        choiceBoxDBFile.setItems(items);
    }


    @FXML
    private void actionSearch(ActionEvent event) {
        loadList();
    }


    @FXML
    private void actionKeyVerifyCode(ActionEvent event) {
        // 显示对话框
        String keyVerifyCode = myAcctService.keyVerifyCode(txtKey.getText());
        CommUI.infoBox(null, "秘钥校验码：" + keyVerifyCode);
    }


    @FXML
    private void actionBackupDB(ActionEvent event) {
        if (isContentChanged) {
            CommUI.warningBox("资料尚未完成保存", "请稍等自动保存，或手动点击保存！");
            listAcct.getSelectionModel().clearAndSelect(lastListFocusId);
            return;
        }

        String bakDBName = choiceBoxDBFile.getValue().getName() + ".myAcctDB"+ ".BAK-" + Long.toString(System.currentTimeMillis(), Character.MAX_RADIX) ;
        try {
            myAcctService.copyFile(choiceBoxDBFile.getValue().getDbFile(), new File(Main.workpath + "/" + bakDBName));
            CommUI.infoBox("备份资料库【" + choiceBoxDBFile.getValue().getName() + "】", "生成备份库：" + bakDBName);
        } catch (IOException e) {
            CommUI.errorBox("备份资料库【" + choiceBoxDBFile.getValue().getName() + "】失败", e.getMessage());
            logger.error("备份资料库【" + choiceBoxDBFile.getValue().getName() + "】失败! " + e.getMessage());
        }
    }

    @FXML
    private void actionNew(ActionEvent event) {
        if (ToolUtil.isEmpty(txtKey.getText())) {
            CommUI.warningBox("资料保护秘钥不能为空！", "为您的资料安全考虑，请先填写【资料保护秘钥】！");
            return;
        }

        if (isContentChanged) {
            CommUI.warningBox("资料尚未完成保存", "请稍等自动保存，或手动点击保存！");
            listAcct.getSelectionModel().clearAndSelect(lastListFocusId);
            return;
        }

        txtAcctName.setText("我的资料" + DateUtil.getTime(new Date()));
        htmlEditor.setHtmlText("模板"); // todo 从模板中建立
        myAcct = new TMyAcct();
        isContentChanged = true;
        myAcct.setName(txtAcctName.getText());
        myAcct.setContent(htmlEditor.getHtmlText());  // 明文
        myAcct = myAcctService.newMyAcct(myAcct, txtKey.getText());

        // 刷新界面
        loadList();

        // 选中刚添加的新数据
        focusCurrentMyAcct();

        changeContent();

        hasNewMyAcct = true; // 首次新建的内容，需要更新
    }

    @FXML
    private void actionSave(ActionEvent event) throws Exception {
        if (ToolUtil.isEmpty(txtAcctName.getText())) {
            CommUI.warningBox("资料保护秘钥不能为空！", "为您的资料安全考虑，请先填写【资料保护秘钥】！");
            return;
        }
        myAcct.setName(txtAcctName.getText());
        myAcct.setContent(htmlEditor.getHtmlText()); // 明文
        myAcct.setDraworder(slider.getValue());
        myAcctService.saveMyAcct(myAcct, txtKey.getText());

        // 刷新界面
        isContentChanged = false;
        changeContent();
        loadList();
    }


    @FXML
    private void actionDelete(ActionEvent event) {
        Optional<ButtonType> result = CommUI.confirmBox("删除资料【" + myAcct.getId() + " - " + myAcct.getName() + "】", "真的要删除吗?");
        if (result.get() == ButtonType.OK) {
            // ... user chose OK
            myAcctService.deleteMyAcct(myAcct);
            loadList();
        } else {
            // ... user chose CANCEL or closed the dialog
            return;
        }
    }

    @FXML
    public void listOnMouseClicked(MouseEvent mouseEvent) throws Exception {
        if (isContentChanged) {
            CommUI.warningBox("资料尚未完成保存", "请稍等自动保存，或手动点击保存！");
            listAcct.getSelectionModel().clearAndSelect(lastListFocusId);
            return;
        }

        if (!validateKey()) {
            return;
        }

        myAcct = listAcct.getSelectionModel().getSelectedItem();
        changeContent();

    }

    @FXML
    public void listOnKeyReleased(KeyEvent keyEvent) throws Exception {
        if (isContentChanged) {
            CommUI.warningBox("资料尚未完成保存", "请稍等自动保存，或手动点击保存！");
            listAcct.getSelectionModel().clearAndSelect(lastListFocusId);
            return;
        }

        if (!validateKey()) {
            return;
        }

        myAcct = listAcct.getSelectionModel().getSelectedItem();
        changeContent();
    }

    /**
     * key 不能为空，不能小于4位字符
     */
    private boolean validateKey() {
        if (ToolUtil.isEmpty(txtKey.getText())) {
            CommUI.warningBox("资料保护秘钥不能为空！", "请先填写【资料保护秘钥】后再选择要查看的资料！");
            labelContentStatus.setText("资料提取失败");
            labelContentStatus.setTextFill(Paint.valueOf("RED"));
            return false;
        }

        if (txtKey.getText().length() < 3) {
            CommUI.warningBox("资料保护秘钥不能少于4个字符！", "请填写超过4字符的【资料保护秘钥】后再选择要查看的资料！");
            labelContentStatus.setText("资料提取失败");
            labelContentStatus.setTextFill(Paint.valueOf("RED"));
            return false;
        }

        return true;
    }

    /**
     * 当更换资料记录时，变更右侧资料内容
     */
    private void changeContent() {
        if (hasNewMyAcct) {
            loadList();
            hasNewMyAcct = false;
        }

        if (ToolUtil.isNotEmpty(myAcct)) {
            labelId.setText(myAcct.getId() + "");
            labelMAC.setText(myAcct.getMac());
            labelKeyVerifyCode.setText(myAcct.getKeyVerifyCode());
            labelCreateDate.setText(myAcct.getCreateDate());
            labelUpdateDate.setText(myAcct.getUpdateDate());
            slider.setValue(myAcct.getDraworder());
            String content = myAcctService.decodeContent(myAcct, txtKey.getText());
            if (ToolUtil.isNotEmpty(content)) {
                // 资料解读成功，可以编辑保存
                htmlEditor.setHtmlText(content);
                htmlEditor.setDisable(false);
                btnSave.setDisable(false);
                slider.setDisable(false);
                labelContentStatus.setText("资料提取成功");
                labelContentStatus.setTextFill(Paint.valueOf("BLACK"));
                // 记录list中对应的id
                lastListFocusId = listAcct.getSelectionModel().getSelectedIndex();
            } else {
                // 资料解读失败，禁用 保存功能，禁用编辑器
                htmlEditor.setHtmlText("!!! 当前资料受到秘钥保护,您不能读取 !!!");
                htmlEditor.setDisable(true);
                btnSave.setDisable(true);
                if (!initialize) {
                    labelContentStatus.setText("资料提取失败");
                    labelContentStatus.setTextFill(Paint.valueOf("RED"));
                    CommUI.warningBox("资料提取失败", "资料秘钥不正确！");
                } else {
                    labelContentStatus.setText("");// 首次状态置空
                    initialize = false;
                }
            }
            txtAcctName.setText(myAcct.getName());
        }
    }

    /**
     * htmlEditor 内容变更
     *
     * @param keyEvent
     */
    public void onContentChanged(KeyEvent keyEvent) {
        // 检查content的内容是否有变更，提示
        logger.trace("资料【" + myAcct.getId() + "】内容有变更");
        isContentChanged = true;
        labelContentStatus.setVisible(true);
        labelContentStatus.setText("资料内容有变更");
        labelContentStatus.setTextFill(Paint.valueOf("RED"));
    }


    /**
     * 每隔5秒检查是否需要保存当前资料
     * <p>
     * schedule(TimerTask task, long delay, long period)
     * 设定指定任务task在指定延迟delay后进行固定延迟peroid的执行
     */
    @FXML
    public void timerSave() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        //更新ui代码
                        if (isContentChanged) {
                            myAcct.setName(txtAcctName.getText());
                            myAcct.setContent(htmlEditor.getHtmlText());
                            myAcct.setDraworder(slider.getValue());
                            myAcctService.saveMyAcct(myAcct, txtKey.getText());
                            // 刷新界面
                            isContentChanged = false;
                            labelContentStatus.setText("资料内容已保存");
                            labelContentStatus.setTextFill(Paint.valueOf("BLUE"));
                            System.out.print("*");
                            loadList();
                            // focusCurrentMyAcct();  // 自动获得焦点 容易影响编辑
                        } else {
                            if (System.currentTimeMillis() / 1000 % 100 > 90) {
                                System.out.println(".");
                            } else {
                                System.out.print(".");
                            }
                        }

                    }
                });
            }
        }, 1000, 5000);
    }

    public void onCompleteKeyInput(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER) {
            if (txtKey.getText().length() < 4) {
                CommUI.warningBox(null, "资料保护秘钥不能少于4位字符！");
                btnNew.setDisable(true);
                btnSave.setDisable(true);
            } else {
                btnNew.setDisable(false);
                btnSave.setDisable(false);
                loadList();
            }
        }
    }

    public void onSliderChange(MouseEvent mouseEvent) {
        logger.debug("资料【" + myAcct.getId() + "】优先级调整：" + Math.round(slider.getValue()));
        isContentChanged = true;
        labelContentStatus.setText("资料优先级调整为：" + Math.round(slider.getValue()));
        labelContentStatus.setTextFill(Paint.valueOf("RED"));
    }

    /**
     * 在列表中定位当前资料
     *
     * @param mouseEvent
     */
    public void onFindMyAcct(MouseEvent mouseEvent) {
        focusCurrentMyAcct();
    }

    /**
     * 在列表中定位当前资料
     */
    private void focusCurrentMyAcct() {
        if (myAcct.getId() > 0) {
            listAcct.requestFocus();  // 设置列表控件获得焦点
            for (int i = 0; i < myAcctList.size(); i++) {
                if (myAcctList.get(i).getId() == myAcct.getId()) {
                    listAcct.getSelectionModel().select(i);
                }
            }
        }
    }

    /**
     * 创建资料库
     *
     * @param actionEvent
     */
    public void actionCreateDB(ActionEvent actionEvent) {
        if (isContentChanged) {
            CommUI.warningBox("资料尚未完成保存", "请稍等自动保存，或手动点击保存！");
            listAcct.getSelectionModel().clearAndSelect(lastListFocusId);
            return;
        }

        String defaultValue = "我的资料库" + DateUtil.formatDate(new Date(), "yyMMddHHmmss");
        String dbName = CommUI.txtInputDlg("新建资料库", "1", "资料库名称：", defaultValue);
        if (ToolUtil.isNotEmpty(dbName)) {
            clearUI();
            myAcctService.createDB(Main.workpath + "/" + dbName + ".myAcctDB");
            loadDBFile();
            loadList();
        }
    }
}
