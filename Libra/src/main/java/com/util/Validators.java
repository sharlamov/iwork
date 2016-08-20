package com.util;

import com.view.component.db.editors.validators.NegativeValidator;
import com.view.component.db.editors.validators.NullValidator;
import com.view.component.db.editors.validators.PositiveValidator;

public class Validators {

    public static NullValidator NULL = new NullValidator(Libra.lng("msg.empty"));
    public static NegativeValidator NEGATIVE = new NegativeValidator(Libra.lng("msg.negative"));
    public static PositiveValidator POSITIVE = new PositiveValidator(Libra.lng("msg.positive"));
}
