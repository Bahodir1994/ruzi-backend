package app.ruzi.service.enums;

import lombok.Getter;

@Getter
public enum DefValues {
    UNIT_DEF(
            "001",
            "Def o'lchov birligi");

    private final String value;
    private final String note;

    DefValues(String value, String note) {
        this.value = value;
        this.note = note;
    }
}
