package com.yun.software.kaadas.UI.bean;


import com.bigkoo.pickerview.model.IPickerViewData;
import com.google.gson.Gson;

import java.util.List;

/**
 * @author 许英俊 2017/4/27
 */

public class ChinaAddress implements IPickerViewData {



    /**
     * id : 1
     * name : 北京市
     * child : [{"id":"1","name":"北京市","child":[{"id":"1","name":"东城区"},{"id":"2","name":"西城区"},{"id":"3","name":"崇文区"},{"id":"4","name":"宣武区"},{"id":"5","name":"朝阳区"}]}]
     */

    private String id;
    private String name;
    private List<ChildBeanX> child;

    public static ChinaAddress objectFromData(String str) {

        return new Gson().fromJson(str, ChinaAddress.class);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ChildBeanX> getChild() {
        return child;
    }

    public void setChild(List<ChildBeanX> child) {
        this.child = child;
    }

    @Override
    public String getPickerViewText() {
        return name;
    }

    public static class ChildBeanX {
        /**
         * id : 1
         * name : 北京市
         * child : [{"id":"1","name":"东城区"},{"id":"2","name":"西城区"},{"id":"3","name":"崇文区"},{"id":"4","name":"宣武区"},{"id":"5","name":"朝阳区"}]
         */

        private String id;
        private String name;
        private List<ChildBean> child;

        public static ChildBeanX objectFromData(String str) {

            return new Gson().fromJson(str, ChildBeanX.class);
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<ChildBean> getChild() {
            return child;
        }

        public void setChild(List<ChildBean> child) {
            this.child = child;
        }

        public static class ChildBean {
            /**
             * id : 1
             * name : 东城区
             */

            private String id;
            private String name;

            public static ChildBean objectFromData(String str) {

                return new Gson().fromJson(str, ChildBean.class);
            }

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }
        }
    }
}
