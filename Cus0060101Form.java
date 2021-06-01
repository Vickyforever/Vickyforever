/*
 * app.tai.taid0070
 *  ファイル名:Cus0060101Form.java
 *
 * 作成者　　:wangyu
 * 作成日　　:202105
 * 最終更新者:
 * 最終更新日:
 *
 */
package com.sofestar.jmsys.bl.customer.controller.cus0060101;

import java.io.Serializable;

import com.sofestar.jmsys.fw.controller.BaseForm;

/**
 * ユーザー情報のフォームクラスです。
 * @author
 */

public class Cus0060101Form extends BaseForm   implements Serializable {

    /** お客様会社名 */

        private String firmName;
      /** お客様名 */

        private String name;

        /** お客様生年月日 */

        private String birthdate;

        private String year;

        private String month;

        private String date;
        /** お客様性別 */

        private String sex;


        /**件数 **/
        public String totalPage;

        /** countStart */
        private String  countStart;

        /** 検索結果フラグ **/
        public String infoKekkaFlg;

        /**  検索フラグ **/
        public String searchBtnFlg;



        /** 変更するアイテムのID **/
        private String toChangeId;

        /**
        更新　新規　フラグ
            */
         private String flag;


        public String getFlag() {
            return flag;
        }

        public void setFlag(String flag) {
            this.flag = flag;
        }

        public String getSearchBtnFlg() {
            return searchBtnFlg;
        }

        public void setSearchBtnFlg(String searchBtnFlg) {
            this.searchBtnFlg = searchBtnFlg;

        }


        public String getFirmName() {
            return firmName;
        }

        public void setFirmName(String firmName) {
            this.firmName = firmName;
        }




        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getBirthdate() {
            return birthdate;
        }

        public void setBirthdate(String birthdate) {
            this.birthdate = birthdate;
        }




        public String getYear() {
            return year;
        }

        public void setYear(String year) {
            this.year = year;
        }

        public String getMonth() {
            return month;
        }

        public void setMonth(String month) {
            this.month = month;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getSex() {
            return sex;
        }

        public void setSex(String Sex) {
            this.sex = Sex;
        }
        public String getCountStart() {
            return countStart;
        }
        public void setCountStart(String countStart) {
            this.countStart = countStart;
        }

        public String getToChangeId() {
            return toChangeId;
        }

        public void setToChangeId(String toChangeId) {
            this.toChangeId = toChangeId;
        }

        public String getTotalPage() {
            return totalPage;
        }
        public void setTotalPage(String totalPage) {
            this.totalPage = totalPage;
        }

        public String getInfoKekkaFlg() {
            return infoKekkaFlg;
        }
        public void setInfoKekkaFlg(String infoKekkaFlg) {
            this.infoKekkaFlg = infoKekkaFlg;
        }


}