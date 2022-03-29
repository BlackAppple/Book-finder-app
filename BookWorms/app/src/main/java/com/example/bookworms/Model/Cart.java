package com.example.bookworms.Model;

public class Cart {
    private  String pid,bookname,price,discount,quantity;

    public Cart() {
    }

    public Cart(String pid, String bookname, String price, String discount, String quantity) {
        this.pid = pid;
        this.bookname = bookname;
        this.price = price;
        this.discount = discount;
        this.quantity = quantity;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getBookname() {
        return bookname;
    }

    public void setBookname(String bookname) {
        this.bookname = bookname;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }
}
