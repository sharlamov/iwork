package com.util;

import com.service.LangService;
import com.view.component.db.editors.validators.NegativeValidator;
import com.view.component.db.editors.validators.NullValidator;
import com.view.component.db.editors.validators.PositiveValidator;

/**
 * Created by sharlamov on 06.04.2016.
 */
public class Validators {

    public static NullValidator NULL = new NullValidator(LangService.trans("msg.empty"));
    public static NegativeValidator NEGATIVE = new NegativeValidator(LangService.trans("msg.negative"));
    public static PositiveValidator POSITIVE = new PositiveValidator(LangService.trans("msg.positive"));
}
