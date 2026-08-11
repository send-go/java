package io.sendgo.model;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.LinkedHashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Contact {
    private String contact;
    private String name;
    private String var1;
    private String var2;
    private String var3;
    private String var4;
    private String var5;
    private String var6;
    private String var7;
    private String var8;
    private Map<String, String> variables;

    public Contact() {}

    private Contact(Builder b) {
        this.contact   = b.contact;
        this.name      = b.name;
        this.var1      = b.var1;
        this.var2      = b.var2;
        this.var3      = b.var3;
        this.var4      = b.var4;
        this.var5      = b.var5;
        this.var6      = b.var6;
        this.var7      = b.var7;
        this.var8      = b.var8;
        this.variables = b.variables;
    }

    public String getContact() { return contact; }
    public String getName()    { return name; }
    public String getVar1()    { return var1; }
    public String getVar2()    { return var2; }
    public String getVar3()    { return var3; }
    public String getVar4()    { return var4; }
    public String getVar5()    { return var5; }
    public String getVar6()    { return var6; }
    public String getVar7()    { return var7; }
    public String getVar8()    { return var8; }

    /** 임의 명명 템플릿 변수 (예: title → 알림톡 #{title}). contact 오브젝트에 평탄화되어 직렬화됨. */
    @JsonAnyGetter
    public Map<String, String> getVariables() { return variables; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String contact, name, var1, var2, var3, var4, var5, var6, var7, var8;
        private Map<String, String> variables;
        public Builder contact(String v) { contact = v; return this; }
        public Builder name(String v)    { name = v; return this; }
        public Builder var1(String v)    { var1 = v; return this; }
        public Builder var2(String v)    { var2 = v; return this; }
        public Builder var3(String v)    { var3 = v; return this; }
        public Builder var4(String v)    { var4 = v; return this; }
        public Builder var5(String v)    { var5 = v; return this; }
        public Builder var6(String v)    { var6 = v; return this; }
        public Builder var7(String v)    { var7 = v; return this; }
        public Builder var8(String v)    { var8 = v; return this; }
        /** 임의 명명 변수 하나 추가 (예: .variable("title", "...")). */
        public Builder variable(String key, String value) {
            if (variables == null) variables = new LinkedHashMap<>();
            variables.put(key, value);
            return this;
        }
        public Builder variables(Map<String, String> m) { this.variables = m; return this; }
        public Contact build() { return new Contact(this); }
    }
}
