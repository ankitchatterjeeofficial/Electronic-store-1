package com.lcdw.electronic.store.Validate;

import com.lcdw.electronic.store.Exception.BadApiRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ImageNameValidator implements ConstraintValidator<ImageNameValid,String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {

        if(value.matches("([^\\s]+(\\.(?i)(jpg|png|gif|bmp|jpeg))$)"))
            return true;
        else {
            return false;
        }
    }
}
