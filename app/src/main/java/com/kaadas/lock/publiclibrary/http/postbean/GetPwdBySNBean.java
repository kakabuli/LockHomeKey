package com.kaadas.lock.publiclibrary.http.postbean;

/**
 * Create By lxj  on 2019/3/3
 * Describe
 */
public class GetPwdBySNBean {

    /**
     * SN : GI132231004
     */

    private String SN;

    public GetPwdBySNBean(String SN) {
        this.SN = SN;
    }

    public String getSN() {
        return SN;
    }

    public void setSN(String SN) {
        this.SN = SN;
    }
}
