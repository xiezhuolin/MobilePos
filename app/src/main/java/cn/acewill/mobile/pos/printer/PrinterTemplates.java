package cn.acewill.mobile.pos.printer;

import android.support.v4.util.ArrayMap;

/**
 * Created by DHH on 2017/3/23.
 */

public class PrinterTemplates {
    // 0：客用小票；1：加菜单；2：退菜单；3：预结单；4：结账单；5：标签；6：厨房单
    private PrintTemplateType templateType;
//    private List<PrintModelInfo> models;
    private ArrayMap<String,PrintModelInfo> models;

    public PrintTemplateType getTemplateType() {
        return templateType;
    }

    public void setTemplateType(PrintTemplateType templateType) {
        this.templateType = templateType;
    }

    public ArrayMap<String, PrintModelInfo> getModels() {
        return models;
    }

    public void setModels(ArrayMap<String, PrintModelInfo> models) {
        this.models = models;
    }

    //    public List<PrintModelInfo> getModels() {
//        return models;
//    }
//
//    public void setModels(List<PrintModelInfo> models) {
//        this.models = models;
//    }
}
