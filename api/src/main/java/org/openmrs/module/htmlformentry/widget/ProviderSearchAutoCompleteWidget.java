package org.openmrs.module.htmlformentry.widget;

import org.openmrs.Provider;
import org.openmrs.module.htmlformentry.FormEntryContext;
import org.openmrs.module.htmlformentry.element.ProviderStub;

import javax.servlet.http.HttpServletRequest;

public class ProviderSearchAutoCompleteWidget implements Widget{
    private Provider initialValue;
    private String src = "providers";

    @Override
    public void setInitialValue(Object initialValue) {
         this.initialValue = (Provider)initialValue;
    }

    @Override
    public String generateHtml(FormEntryContext context) {

        StringBuilder sb = new StringBuilder();
        if (context.getMode().equals(FormEntryContext.Mode.VIEW)) {
            String toPrint = "";
            if (initialValue != null) {
                toPrint = initialValue.getName();
                return WidgetFactory.displayValue(toPrint);
            } else {
                return WidgetFactory.displayDefaultEmptyValue();
            }
        } else {
            sb.append("<input type=\"text\"  id=\""
                    + context.getFieldName(this) + "\"" + " name=\""
                    + context.getFieldName(this) + "\" "
                    + " onfocus=\"setupProviderAutocomplete(this, '" + this.src + "');\""
                    + "class=\"autoCompleteText\""
                    + "onchange=\"setValWhenAutocompleteFieldBlanked(this)\""
                    + " onblur=\"onBlurAutocomplete(this)\"");

            if (initialValue != null)
                sb.append(" value=\"" + (new ProviderStub(initialValue).getDisplayValue()) + "\"");
            sb.append("/>");

            sb.append("<input name=\"" + context.getFieldName(this) + "_hid"
                    + "\" id=\"" + context.getFieldName(this) + "_hid" + "\""
                    + " type=\"hidden\" class=\"autoCompleteHidden\" ");
            if (initialValue != null) {
                sb.append(" value=\"" + initialValue.getProviderId() + "\"");
            }
            sb.append("/>");
        }
        return sb.toString();
    }

    @Override
    public Object getValue(FormEntryContext context, HttpServletRequest request) {
        return request.getParameter(context.getFieldName(this)+"_hid");
    }
}
