package com.macrolab.myAcct.controller;

import com.macrolab.myAcct.Main;
import com.macrolab.myAcct.common.CommUI;
import com.macrolab.myAcct.model.TMyAcct;
import com.macrolab.myAcct.service.MyAcctService;
import com.macrolab.myAcct.util.DateUtil;
import com.macrolab.myAcct.util.ToolUtil;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Paint;
import javafx.scene.web.HTMLEditor;

import java.net.URL;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainController implements Initializable {

    @FXML
    public HTMLEditor htmlEditor;
    @FXML
    public Label labelId;
    @FXML
    public Slider slider;
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
    private ChoiceBox<?> choiceDBFile;
    @FXML
    private PasswordField txtKey;
    @FXML
    private Button btnKeyVerifyCode;
    @FXML
    private Button btnCreateAcct;
    @FXML
    private Button btnBackupAcct;
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
    private Label labelContentChanged;

    // 当前编辑的数据
    TMyAcct myAcct;

    // 当前内容缓存，用于与变更内容对比 todo 变更内容对比
    TMyAcct lastMyAcct;

    // 当前内容是否有变更
    boolean isContentChanged = false;

    // 初始化标志
    boolean initialize = true;


    private Main application;

    private MyAcctService myAcctService;

    public void setMyAcctService(MyAcctService myAcctService) {
        this.myAcctService = myAcctService;
    }

    public void setApp(Main application) {
        this.application = application;
    }

    /**
     * 界面初始化
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Logger.getLogger(MainController.class.getName()).log(Level.INFO, "MainController initialize ... ");
        clearUI();
        timerSave();  // 启动自动保存线程
        initialize = false;  // 初始化结束
    }

    /**
     * 初始化界面控件状态
     */
    private void clearUI() {
        myAcct = new TMyAcct();
        changeContent();
        labelContentChanged.setVisible(false);
        labelId.setText("资料编号");
        labelContentChanged.setText("");
        htmlEditor.setDisable(true);
        btnSave.setDisable(true);
        btnBackupAcct.setDisable(true);
        slider.setValue(0);
    }

    /**
     * 初始化读取数据，加载到页面
     */
    public void loadList() {
        List<TMyAcct> list = myAcctService.queryMyAcct(txtSearch.getText(), "");
        ObservableList<TMyAcct> items = FXCollections.observableArrayList(list);
        listAcct.setItems(items);
    }


    @FXML
    private void actionSearch(ActionEvent event) {
        loadList();
    }

    @FXML
    private void listOnEditStart(ListView.EditEvent<String> event) {
    }

    @FXML
    private void actionKeyVerifyCode(ActionEvent event) {
        // 显示对话框
        String keyVerifyCode = myAcctService.keyVerifyCode(txtKey.getText());
        CommUI.infoBox(null, "秘钥校验码：" + keyVerifyCode);
    }

    @FXML
    private void actionCreateAcct(ActionEvent event) {
    }

    @FXML
    private void actionBackupAcct(ActionEvent event) {
    }

    @FXML
    private void actionNew(ActionEvent event) {
        if (ToolUtil.isEmpty(txtKey.getText())) {
            CommUI.warningBox("资料保护秘钥不能为空！", "为您的资料安全考虑，请先填写【资料保护秘钥】！");
            return;
        }
        txtAcctName.setText("我的资料" + DateUtil.getTime(new Date()));
        htmlEditor.setHtmlText("模板"); // todo 从模板中建立
        myAcct = new TMyAcct();
        myAcct.setName(txtAcctName.getText());
        myAcct.setContent(htmlEditor.getHtmlText());
        myAcctService.newMyAcct(myAcct, txtKey.getText());

        // 刷新界面
        isContentChanged = false;
        changeContent();
        loadList();
    }

    @FXML
    private void actionSave(ActionEvent event) throws Exception {
        if (ToolUtil.isEmpty(txtAcctName.getText())) {
            CommUI.warningBox("资料保护秘钥不能为空！", "为您的资料安全考虑，请先填写【资料保护秘钥】！");
            return;
        }
        myAcct.setName(txtAcctName.getText());
        myAcct.setContent(htmlEditor.getHtmlText());
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
        // todo 获取上一次的 myAcct


        if (ToolUtil.isEmpty(txtKey.getText())) {
            CommUI.warningBox("资料保护秘钥不能为空！", "请先填写【资料保护秘钥】后再选择要查看的资料！");
            return;
        }
        myAcct = listAcct.getSelectionModel().getSelectedItem();
        changeContent();

    }

    @FXML
    public void listOnKeyReleased(KeyEvent keyEvent) throws Exception {
        // todo 获取上一次的 myAcct
//        lastMyAcct = myAcct.getContent();
//        listAcct.get

        if (ToolUtil.isEmpty(txtKey.getText())) {
            CommUI.warningBox("资料保护秘钥不能为空！", "请先填写【资料保护秘钥】后再选择要查看的资料！");
            return;
        }
        myAcct = listAcct.getSelectionModel().getSelectedItem();
        changeContent();
    }

    /**
     * 当更换资料记录时，变更右侧资料内容
     */
    private void changeContent() {
        if (isContentChanged) {
            // todo 内容有变更，是否保存
        }

        if (ToolUtil.isNotEmpty(myAcct)) {
            labelId.setText(myAcct.getId() + "");
            labelMAC.setText(myAcct.getMac());
            labelKeyVerifyCode.setText(myAcct.getKeyVerifyCode());
            labelCreateDate.setText(myAcct.getCreateDate());
            labelUpdateDate.setText(myAcct.getUpdateDate());
            labelContentChanged.setText("");
            slider.setValue(myAcct.getDraworder());
            String content = "";
            if (ToolUtil.isNotEmpty(myAcctService)) {
                content = myAcctService.decodeContent(myAcct, txtKey.getText());
            }

            if (ToolUtil.isNotEmpty(content)) {
                // 资料解读成功，可以编辑保存
                htmlEditor.setHtmlText(content);
                htmlEditor.setDisable(false);
                btnSave.setDisable(false);
            } else {
                // 资料解读失败，禁用 保存功能，禁用编辑器
                htmlEditor.setHtmlText("!!! 当前资料受到秘钥保护,您不能读取 !!!");
                htmlEditor.setDisable(true);
                btnSave.setDisable(true);
//                if (!initialize) {
//                    CommUI.errorBox("资料解密失败！", "你正在读取的资料受到秘钥保护，与您使用的秘钥不符，不能解读当前资料内容！");
//                }
            }
            txtAcctName.setText(myAcct.getName());
        }
    }

    public void onContentChanged(KeyEvent keyEvent) {
        // 检查content的内容是否有变更，提示
        isContentChanged = true;
        labelContentChanged.setVisible(true);
        labelContentChanged.setText("资料内容有变更");
        labelContentChanged.setTextFill(Paint.valueOf("RED"));
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
                            labelContentChanged.setText("资料内容已保存");
                            labelContentChanged.setTextFill(Paint.valueOf("BLUE"));
                            System.out.print("*");
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
}
