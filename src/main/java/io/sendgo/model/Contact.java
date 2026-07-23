package io.sendgo.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Contact {
    private String contact;
    private String name;
    private String var1;
    private String var2;
    private String var3;
    private String var4;
    private String var5;

    public Contact() {}

    private Contact(Builder b) {
        this.contact = b.contact;
        this.name    = b.name;
        this.var1    = b.var1;
        this.var2    = b.var2;
        this.var3    = b.var3;
        this.var4    = b.var4;
        this.var5    = b.var5;
    }

    public String getContact() { return contact; }
    public String getName()    { return name; }
    public String getVar1()    { return var1; }
    public String getVar2()    { return var2; }
    public String getVar3()    { return var3; }
    public String getVar4()    { return var4; }
    public String getVar5()    { return var5; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String contact, name, var1, var2, var3, var4, var5;
        public Builder contact(String v) { contact = v; return this; }
        public Builder name(String v)    { name = v; return this; }
        public Builder var1(String v)    { var1 = v; return this; }
        public Builder var2(String v)    { var2 = v; return this; }
        public Builder var3(String v)    { var3 = v; return this; }
        public Builder var4(String v)    { var4 = v; return this; }
        public Builder var5(String v)    { var5 = v; return this; }
        public Contact build() { return new Contact(this); }
    }
}
