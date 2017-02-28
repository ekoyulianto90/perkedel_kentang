package com.skripsi.ekoyulianto.biologipedia.Result;

/**
 * Created by ekoyulianto on 12/30/2016.
 */

public class Result {

    public int id;
    public String istilah;
    public String deskripsi;

    public Result() {
        this.id = id;
        this.istilah = istilah;
        this.deskripsi = deskripsi;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIstilah() {
        return istilah;
    }

    public void setIstilah(String istilah) {
        this.istilah = istilah;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }
}
