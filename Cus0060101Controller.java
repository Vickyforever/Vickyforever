 /*
* パッケージ名:com.sofestar.jmsys.bl.customer.controller
 * ファイル名:Cus0060101Controller.java
 *

* 作成者　　:wangyu
* 作成日　　:202105
* 最終更新者:
* 最終更新日:
 *
 */
package com.sofestar.jmsys.bl.customer.controller.cus0060101;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;
import org.supercsv.quote.AlwaysQuoteMode;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.sofestar.jmsys.bl.common.bean.CodeItem;
import com.sofestar.jmsys.bl.common.consts.JmConsts;
import com.sofestar.jmsys.bl.common.consts.ViewConsts;
import com.sofestar.jmsys.bl.common.utils.SSDateUtils;
import com.sofestar.jmsys.bl.common.utils.Utils;
import com.sofestar.jmsys.bl.customer.bean.dto.cus0060101.Cus0060101SearchDto;
import com.sofestar.jmsys.bl.customer.bean.io.cus0060101.Cus0060101SearchInput;
import com.sofestar.jmsys.bl.customer.bean.io.cus0060101.Cus0060101SearchOutput;
import com.sofestar.jmsys.bl.customer.service.cus0060101.Cus0060101Service;
import com.sofestar.jmsys.fw.RequestContext;
import com.sofestar.jmsys.fw.bean.SessionInfo;
import com.sofestar.jmsys.fw.controller.BaseController;

/**
 * ユーザー情報コントローラーです。
 *
 */
@Controller
@RequestMapping("cus0060101")
public class Cus0060101Controller extends BaseController {

    // =================================================================================================================
    // インスタンス変数
    // =================================================================================================================
    // ---------------------------------------------------------------------------------------------------------
    // private
    @Autowired
    private Cus0060101Service cus0060101Service;

    @Value("${temp_download_path}")
    private String tempPath;

    // 日付YYMMDDフォーマット： */
    private static final String   YYMMDD_FORMAT = "yyyy/MM/dd";

    // =================================================================================================================
    // インスタンスメソッド
    // =================================================================================================================
    // ----------------------------------------------------------------------------------------------------------
    // public
    /**
     * 初期処理を行います
     *
     * @param Model
     *            model
     * @return View
     */
    @RequestMapping(value = "/")
    public String init(Model model) throws Exception {

        // フォームを生成
        Cus0060101Form form = new Cus0060101Form();

        setItems( model );

//        this.setbirthDate(form, model);

        //初期データを表示する
        form.setFirmName("firm");
        form.setName("name");

        if ("1".equals(form.getFlag())) {
                    search(form, model);
        } else {
                    form.setFlag("0");
                    form.setToChangeId(null);
        }

//        form.setbirthdate( setbirthDate(form, model) );
//        form.setbirthdate(ViewConsts.DEFAULT_BIRTHDAY); 定数不可

           //カレント年度、月、日を設定する设定当前时间
        form.setYear(SSDateUtils.getSystemCurYear());
        form.setMonth(SSDateUtils.getSystemCurMonth());
        form.setDate(SSDateUtils.getSystemCurDay());

        form.setSex("3");

        model.addAttribute(form);

        form.setCountStart("0");
        form.setDispNum(ViewConsts.DEFAULT_DISP_NUM);
        form.setPageText("1");
        form.setPageIti("1");
        form.setPageItiRange("0");

        setitem(model);

//        List<String> sexList=new ArrayList<String>();
//        sexList.add("男");
//        sexList.add("女");
//        sexList.add("保密");
//        // 存储 性别的集合
//        model.addAttribute("sexList",sexList);

        // ビューを返却
        return getView(model, null);
    }

    /**
     * `お客様情報がDBにいない場合データベース入力を行います
     *   新規入力insert  Flag判断存在的同时更新
     * @param form
     * @param model
     * @return View
     */
    @RequestMapping(value = "/update")
    public String update(@Valid
            @ModelAttribute Cus0060101Form form,
            BindingResult result, Model model)
            throws Exception {
        //  设定并保持情报入力框的初始条件  not null
        setItems( model );

        form.setName( Utils.trim( form.getName() ));

        if ( Utils.isEmpty(form.getName()) ) {
            //name入力必須
            model.addAttribute(JmConsts.OPERATION_MSG, getFormatMessage("CMN_MSG_0006", "お客様名name") );
            return getView(model, result);
        }

        //エラーがある場合
        if (result.hasErrors()) {
            return getView(model, result);
        }

        //updateidを取る  ???key
        SessionInfo si = RequestContext.getContext().getSessionAttribute(
                SessionInfo.SESSION_KEY);

        Cus0060101SearchDto cus0060101SearchDto = (Cus0060101SearchDto)si.getSessionAttribute("cus0060101SearchDto");

        //お客様名などを全角変換する、数値を全角から半角に
        resetInputItemValue(form);

        //  设定并保持情报入力框的初始条件  not null
        setItems( model );


        //既存の場合
        if ("1".equals(form.getFlag())) {
            Cus0060101SearchInput input = new Cus0060101SearchInput();
            BeanUtils.copyProperties(form, input);

            //nameが変わる場合
            if ( !form.getName().equals(form.getToChangeId() )) {
                //新規nameであるnameで既存チェックする
                if ( this.cus0060101Service.isAgencyExsi( input ) ){
                    //番号重複
                    model.addAttribute(JmConsts.OPERATION_MSG, getFormatMessage("CMN_MSG_0004", "お客様名") );
                }else {
                    this.cus0060101Service.update(input, form.getToChangeId() );
                    //情報を更新
                    model.addAttribute(JmConsts.OPERATION_MSG,  getFormatMessage("CMN_MSG_0002", "お客様情報"));
                    form.setFlag("1");
                    form.setToChangeId(form.getName());
                }
            }else {
                this.cus0060101Service.update(input,form.getToChangeId());
                //情報を更新
                model.addAttribute(JmConsts.OPERATION_MSG, getFormatMessage("CMN_MSG_0002", "お客様情報"));
                form.setFlag("1");
                form.setToChangeId(form.getName());
            }
        }

        //新規の場合
        if ("0".equals(form.getFlag())) {
            Cus0060101SearchInput input = new Cus0060101SearchInput();
            BeanUtils.copyProperties(form, input);

            //nameで既存チェックする
            if ( this.cus0060101Service.isAgencyExsi( input ) ){
                form.setFlag("0");
                //重複
                model.addAttribute(JmConsts.OPERATION_MSG, getFormatMessage("CMN_MSG_0004", "お客様名") );
            }else {
                this.cus0060101Service.creat(input);
                //情報を新規作成
                model.addAttribute(JmConsts.OPERATION_MSG, getFormatMessage("CMN_MSG_0001", "お客様情報") );
                form.setFlag("1");
                form.setToChangeId(form.getName());
            }
        }

        // ビューを返却
        return getView(model, null);

    }

    /**
     * 登録内容の半角から、全角に変更
     * @param cus0060101Form
     */
    private void resetInputItemValue(Cus0060101Form cus0060101Form){
        //お客様会社名半角ー＞全角
        if ( Utils.isNotEmpty( cus0060101Form.getFirmName())) {
            cus0060101Form.setFirmName(Utils.hankakuKana2zenkakuKana(cus0060101Form.getFirmName()));
        }
        //生年月日、性別の全角数値を半角数値に変更
//        Birthdate是Date不是STRING
        if ( Utils.isNotEmpty( cus0060101Form.getBirthdate())) {
            cus0060101Form.setBirthdate(Utils.digiZenkakuKana2hankakuKana(cus0060101Form.getBirthdate()));
        }

        if ( Utils.isNotEmpty( cus0060101Form.getYear() )) {
            cus0060101Form.setYear(Utils.digiZenkakuKana2hankakuKana(cus0060101Form.getYear()));
        }

        if ( Utils.isNotEmpty( cus0060101Form.getMonth() )) {
            cus0060101Form.setMonth(Utils.digiZenkakuKana2hankakuKana(cus0060101Form.getMonth()));
        }

        if ( Utils.isNotEmpty( cus0060101Form.getDate() )) {
            cus0060101Form.setDate(Utils.digiZenkakuKana2hankakuKana(cus0060101Form.getDate()));
        }
        if ( Utils.isNotEmpty( cus0060101Form.getSex() )) {
            cus0060101Form.setSex(Utils.digiZenkakuKana2hankakuKana(cus0060101Form.getSex()));
        }
    }


    /**
     * 同じ画面で検索を行います。
     *
     * @param form
     * @param model
     * @return View
     */
    @RequestMapping(value = "/search")
    public String search(@ModelAttribute Cus0060101Form form,    Model model     ) throws Exception {

        //検索の空白を除却(お客様会社名、お客様名 )
        form.setFirmName(Utils.trim(form.getFirmName()));
        form.setName(Utils.trim(form.getName()));


        SessionInfo session = RequestContext.getContext().getSessionAttribute(
                SessionInfo.SESSION_KEY);
//  设定并保持情报入力框的初始条件  not null
    setItems( model );
        //検索条件をセッションに保存
        session.setSessionAttribute("searchiteAgent", form.getFirmName());
        session.setSessionAttribute("searchiteTel", form.getName());

        //検索する
        searchItems(form, model);

        // ビューを返却
        return getView(model, null);
    }



    /**
     *DBを検索して、結果をモデルに設定する
     */
    private void searchItems(Cus0060101Form form, Model model)
            throws Exception {

        //フォームの値を変換する  * 半角カナ→全角カナ変換を行います。
        if (Utils.isNotEmpty(form.getFirmName())) {
            form.setFirmName(Utils.hankakuKana2zenkakuKana(form.getFirmName()));
        }
        if (Utils.isNotEmpty(form.getName())) {
            form.setName(Utils.digiZenkakuKana2hankakuKana(form.getName()));
        }


        Cus0060101SearchInput input = new Cus0060101SearchInput();

        if ( Utils.isEmpty(form.getPageText()) ||
                Utils.isEmpty(form.getDispNum()) ) {
               form.setCountStart("0");
               form.setDispNum(ViewConsts.DEFAULT_DISP_NUM);
               form.setPageText("1");
               form.setPageIti("1");
               form.setPageItiRange("0");
           }else {
               form.setCountStart(String.valueOf(Integer.parseInt(form.getPageText())
                       * Integer.parseInt(form.getDispNum())
                       - Integer.parseInt(form.getDispNum())));
           }
//        setitem(model);
        BeanUtils.copyProperties(form, input);

        Cus0060101SearchOutput cus0060101SearchOutput = this.cus0060101Service.search(input);
        BeanUtils.copyProperties(cus0060101SearchOutput, form);
//防止空格输入
//        if ( null != cus0060101SearchOutput.getList()
//                &&  0 < cus0060101SearchOutput.getList().size() ) {
//            Cus0060101SearchDto aCus0060101SearchDto = cus0060101SearchOutput.getList().get(0);
//            BeanUtils.copyProperties(Dto, form);
//        }

        if ( null != cus0060101SearchOutput.getList()
                &&  0 < cus0060101SearchOutput.getList().size() ) {
            form.setInfoKekkaFlg("1");
            form.setTotalPage(cus0060101SearchOutput.getList().get(0).getTotalPage());
        }else {
            //検索結果がゼロ件の場合
            // 検索結果フラグ
            form.setInfoKekkaFlg("0");
            form.setTotalPage("0");//0件
            model.addAttribute(JmConsts.OPERATION_MSG, ViewConsts.NO_RESULT_FOUND);
        }
        model.addAttribute("lists", cus0060101SearchOutput.getList() );
        model.addAttribute("toChangeId", form.getToChangeId());
    }



            /**
             * CSV出力<BR>
             *
             * @param form
             * @param result
             * @param request
             * @param response
             * @param model
             * @param redirectAttrs
             * @return View
             * @throws InvalidFormatException エラー
             * @throws FileNotFoundException エラー
             * @throws IOException エラー
             */
            @RequestMapping(value = "/print")
            public void csvDownLoad(@ModelAttribute Cus0060101Form form,
                                  Model model,
                                  HttpServletRequest request,
                                  HttpServletResponse httpServletResponse) throws Exception {

                // モデルオブジェクトを初期設定
                setitem(model);

                // 検索条件の設定
                Cus0060101SearchInput input = new Cus0060101SearchInput();

                // 検索を実行
                Cus0060101SearchOutput output = this.cus0060101Service.searchCsv(input);

//                input.setBirthdate(Utils.formatDate(Utils.getDateByString(input.birthdate))) ;
//                设置文件名
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
                String outputcsvFileName = "Cus0060101-";

                outputcsvFileName += dateFormat.format(new Date());
                outputcsvFileName += ".csv";

                httpServletResponse.setContentType("application/octet-stream");

                httpServletResponse.addHeader("Content-Disposition", "attachment; filename=\"" +  Utils.getDownloadFileName( outputcsvFileName ) + "\"");
                BufferedInputStream bis = null;
                BufferedOutputStream out = null;
                String outputcsvFilePath = this.tempPath + File.separator + outputcsvFileName;
                File outputcsvFile = new File(outputcsvFilePath);


                FileOutputStream fos  = new FileOutputStream( outputcsvFile );
                OutputStreamWriter osw = new OutputStreamWriter(fos, "Shift_JIS");


                CsvPreference preference = new CsvPreference.Builder(
                        CsvPreference.STANDARD_PREFERENCE).useQuoteMode(
                        new AlwaysQuoteMode()).build();

                ICsvBeanWriter csvWriter = new CsvBeanWriter( osw,   preference );

                String[] head = {"お客様会社名","お客様名","お客様生年月日","お客様性別"};

                csvWriter.writeHeader(head);

                String[] header = { "firmName", "name", "birthdate", "sex"};


                List<Cus0060101SearchDto> list = output.getList();

                for (Cus0060101SearchDto d : list) {

                    csvWriter.write(d, header);
                    d.setBirthdate(Utils.getDateByString(Utils.formatDate(d.birthdate))) ;

                }


                //ファイルを閉じる
                if ( null != csvWriter ) {
                    csvWriter.close();
                }
                if ( null != osw ) {
                    osw.close();
                }
                if ( null != fos ) {
                    fos.close();
                }

                try {
                    bis = new BufferedInputStream(new FileInputStream( outputcsvFile ) );
                    out = new BufferedOutputStream(httpServletResponse.getOutputStream());
                    byte[] buff = new byte[2048];
        //          out.write(new   byte []{( byte ) 0xEF ,( byte ) 0xBB ,( byte ) 0xBF });

                    while (true) {
                      int bytesRead;
                      if (-1 == (bytesRead = bis.read(buff, 0, buff.length))){
                          break;
                      }

                      out.write(buff, 0, bytesRead);
                    }

                    outputcsvFile.deleteOnExit();

                }
                catch (IOException e) {
                    throw e;
                }
                finally{

                    try {

                        if(bis != null){
                            bis.close();
                        }
                        if(out != null){

                            out.flush();
                            out.close();

                        }


                    }
                    catch (IOException e) {

                        throw e;
                    }
                }
            }


            /**
             * 削除　検索を行います
             *
             * @param form
             * @param model
             * @return View
             */
            @RequestMapping(value = "/dodelete")
            public String dodelete( @ModelAttribute Cus0060101Form form,    Model model     ) throws Exception {

                setItems( model );
                Cus0060101SearchInput input = new Cus0060101SearchInput();

                BeanUtils.copyProperties(form, input);
                 this.cus0060101Service.delet(input);
                 //"お客様情報を削除
                 model.addAttribute(JmConsts.OPERATION_MSG, getFormatMessage("CMN_MSG_0003", "お客様情報"));
                // ビューを返却
                return getView(model, null);

            }
//            @RequestMapping("/delete")
//            public String delete(@ModelAttribute Sys100Form userParam, Model model) {
//                setItems(model);
//                setLoginUserName(model);
//                Sys100Input input = new Sys100Input();
//                input.setId(userParam.getToChangeId());
//
//                sys100Service.delete(input.getId());
//
//                searchAgentList(userParam, model);
//
//                return ViewConsts.S100_LIST;
//
//            }

            /**
             * リストなどの項目を設定する
             * @param model
             */
            private void setitem(Model model) {
                // 表示する件数を入れる
                model.addAttribute("roleMapDispNum", ViewConsts.roleMapDispNum);
        //        model.addAttribute("mainmenu_functionId", ViewConsts.MAINMENU_SYSTEM);
        //        model.addAttribute("submenu_functionId", ViewConsts.SUBMENU_SYK0050401);
            }

            /**
                *设置当前月份的天数下拉框包含的天数
             */
            public static Map<String, String> getBusinessDayMap() {
                Map<String, String> businssDayMap = new LinkedHashMap<String, String>();

        //       0-9日前面加0，变成两位数
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



                // 性别 radiobuttons
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
             * @param model Modelオブジェクト
             * @param result バインディング結果
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

                return "customer/cus0060101";

            }
        }


