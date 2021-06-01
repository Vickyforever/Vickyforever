/*
 * パッケージ名:com.sofestar.jmsys.bl.customer.controller
 * ファイル名:Cus0060102Controller.java
 *
 * 作成者　　:SOFESTAR Co.LTD
 * 作成日　　:
 * 最終更新者:
 * 最終更新日:
 *
 */
package com.sofestar.jmsys.bl.customer.controller.cus0060102;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sofestar.jmsys.bl.common.bean.CodeItem;
import com.sofestar.jmsys.bl.common.consts.ViewConsts;
import com.sofestar.jmsys.bl.common.utils.SSDateUtils;
import com.sofestar.jmsys.bl.customer.bean.io.cus0060102.Cus0060102SearchInput;
import com.sofestar.jmsys.bl.customer.service.cus0060102.Cus0060102Service;
import com.sofestar.jmsys.domain.model.cmt.Tcustomerinfo;
import com.sofestar.jmsys.fw.controller.BaseController;

/**
 * お客様の基本情報登録コントローラーです。
 *
 * @author 株式会社
 */
@Controller
@RequestMapping("cus0060102")
public class Cus0060102Controlller extends BaseController  {

    @Autowired
    Cus0060102Service cus0060102Service;

    // =================================================================================================================
    // インスタンスメソッド
    // =================================================================================================================
    // -----------------------------------------------------------------------------------------------------------------
    // public
    /**
     * 初期処理を行います
     *
     * @param Model
     *            model
     * @return View
     */
    @RequestMapping(value = "/")
    public String init(@ModelAttribute Cus0060102Form form, Model model)
            throws Exception {

        setItems( model );

        if ("1".equals(form.getFlag())) {
            search(form, model);
        } else {
            form.setFlag("0");
            form.setToChangeId(null);
        }

        //改ページのデータも設定
        form.setPageText("1");
        form.setPageIti("1");
        form.setPageItiRange("0");

        // ビューを返却
        return getView(model, null);
    }

    /**
     * 検索を行います
     *
     * @param form
     * @param model
     * @return View
     */
    @RequestMapping(value = "/search")
    public String search(@Valid @ModelAttribute Cus0060102Form form, Model model)
            throws Exception {

        setItems( model );
        Cus0060102SearchInput input = new Cus0060102SearchInput();
        input.setName(form.getToChangeId());

       Tcustomerinfo output = cus0060102Service.search(input);

        if(output != null){
            BeanUtils.copyProperties(output, form);

            form.setFirmName(output.getFirmname());
        }

//        需要添加什么message?
        return ViewConsts.CUSTOMER_LIST;

//        Cus0060102SearchOutput output = this.cus0060102Service.search(input);
//        BeanUtils.copyProperties(output, form);
//        form.setFlag("1");
//        // ビューを返却
//        return getView(model, null);
    }

    /**
     * リストなどの項目を設定する
     * @param model
     */
    private void setitem(Model model) {
        // 表示する件数を入れる
        model.addAttribute("map12Month", ViewConsts.map12Month);
        model.addAttribute("mainmenu_functionId", ViewConsts.MAINMENU_CUSTOMER);
        model.addAttribute("submenu_functionId", ViewConsts.SUBMENU_CUS0060102);
    }

    /**
     *设置当前月份的天数下拉框包含的天数
  */
 public static Map<String, String> getBusinessDayMap() {
     Map<String, String> businssDayMap = new LinkedHashMap<String, String>();

//    0-9日前面加0，变成两位数
     for(int i = 1; i <= 9; i++) {
         businssDayMap.put( ("0" + String.valueOf(i)), ("0" + String.valueOf(i)) );
     }

    for(int i = 10; i <= 31; i++) {
         businssDayMap.put( String.valueOf(i),  String.valueOf(i));
     }

     return businssDayMap;
 }
    /**
     * 画面項目を設定
     *
     * @param model
     *            Modelオブジェクト
     */
    private void setItems(Model model) {

        //請求対象年
        model.addAttribute("selectStartYear", SSDateUtils.getBusinessYearMap( 40 ) );

        //請求対象月
        model.addAttribute("selectStartMonthlist", SSDateUtils.getBusinessMonthMap() );

        //請求対象日
        model.addAttribute("selectStartDay", getBusinessDayMap() );



        // 性别 radiobutton
        CodeItem codeRadioMode = new CodeItem();
        List<CodeItem> mapradioMode = new ArrayList<>();

        codeRadioMode.setCode("1");
        codeRadioMode.setName("男");
        mapradioMode.add(codeRadioMode);

        codeRadioMode = new CodeItem();
        codeRadioMode.setCode("2");
        codeRadioMode.setName("女");
        mapradioMode.add(codeRadioMode);

        codeRadioMode = new CodeItem();
        codeRadioMode.setCode("3");
        codeRadioMode.setName("未定");
        mapradioMode.add(codeRadioMode);

        model.addAttribute("sex_radio_items", mapradioMode);



    }
    /**
     * View名を取得します
     *
     * @param model
     *            Modelオブジェクト
     * @param result
     *            バインディング結果
     * @return View名
     */

    private String getView(Model model, BindingResult result) {
        // ログインユーザ名を設定
        setLoginUserName(model);
        setitem(model);
        // バインディング結果をModelオブジェクトに格納
        if (result != null) {
            model.addAttribute(BindingResult.class.getName() + ".errorForm",
                    result);
        }
        return "customer/cus0060102";

    }

}
