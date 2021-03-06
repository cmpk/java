package validation;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.apache.commons.csv.CSVRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import csv.ICSVRecord;

class IPv4AddressValidatorTest {
    private Validator validator = null;

    @BeforeEach
    public final void beforeAll() {
        this.validator = ValidationUtility.getValidator();
    }

    @Test
    @DisplayName("値がNullの場合にエラーにならないこと")
    public final void testPositive_whenNull() {
        AcceptNullBean bean = new AcceptNullBean(null);
        Set<ConstraintViolation<ICSVRecord>> violations = this.validator.validate(bean);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("値がNullの場合にエラーとなること")
    public final void testNegative_whenNull() {
        NotNullBean bean = new NotNullBean(null);
        Set<ConstraintViolation<ICSVRecord>> violations = this.validator.validate(bean);
        assertFalse(violations.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "0", "0.0", "0.0.0", "0.0.0.", "a.0.0.0"})
    @DisplayName("値がIPアドレスの形式でない場合にエラーとなること")
    public final void testNegative_whenInvalidFormat(final String str) {
        NotNullBean bean = new NotNullBean(str);
        Set<ConstraintViolation<ICSVRecord>> violations = this.validator.validate(bean);
        assertFalse(violations.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {"0.0.0.0", "12.12.12.12", "255.255.255.255"})
    @DisplayName("値がIPアドレスの形式の場合にエラーにならないこと")
    public final void testPositive(final String str) {
        NotNullBean bean = new NotNullBean(str);
        Set<ConstraintViolation<ICSVRecord>> violations = this.validator.validate(bean);
        assertTrue(violations.isEmpty());
    }

    private static class AcceptNullBean implements ICSVRecord {
        @IPv4Address(nullable = true)
        private String ipv4Address = null;

        AcceptNullBean(final String value) {
            this.ipv4Address = value;
        }

        @Override
        public void setRecord(final CSVRecord record) {
            // pass
        }

        @Override
        public int getColumnSize() {
            return 0;
        }

        @Override
        public String[] getRecord() {
            return null;
        }
    }

    private static class NotNullBean implements ICSVRecord {
        @IPv4Address(nullable = false)
        private String ipv4Address = null;

        NotNullBean(final String value) {
            this.ipv4Address = value;
        }

        @Override
        public void setRecord(final CSVRecord record) {
            // pass
        }

        @Override
        public int getColumnSize() {
            return 0;
        }

        @Override
        public String[] getRecord() {
            return null;
        }
    }
}
