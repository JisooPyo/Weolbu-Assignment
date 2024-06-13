package weolbu.assignment.validation;

import java.util.Iterator;
import java.util.Set;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

public class ValidationCustomUtils {

    private final Validator validator;

    public ValidationCustomUtils() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    public <T> String getValidationExMessage(T dto) {
        Set<ConstraintViolation<T>> validate = validator.validate(dto);
        Iterator<ConstraintViolation<T>> iter = validate.iterator();
        if (iter.hasNext()) {
            ConstraintViolation<T> violation = iter.next();
            return violation.getMessage();
        }
        return null;
    }
}

