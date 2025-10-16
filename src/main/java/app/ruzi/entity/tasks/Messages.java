package app.ruzi.entity.tasks;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;

@Entity
@Table(name = "messages", schema = "ruzi")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Messages {

    @Id
    @Column(name = "code", length = 11)
    private String code;

    @Column(name = "name_uz", length = 600)
    private String nameUz;

    @Column(name = "name_oz", length = 600)
    private String nameOz;

    @Column(name = "name_ru", length = 600)
    private String nameRu;

    @Column(name = "name_en", length = 600)
    private String nameEn;

    @Transient
    private String name;

    @PostLoad
    private void initializeDefaultStatus() {
        Locale currentLocale = LocaleContextHolder.getLocale();
        String lang = currentLocale.getLanguage();

        switch (lang) {
            case "uz" -> name = getNameUz();
            case "oz" -> name = getNameOz();
            case "ru" -> name = getNameRu();
            case "en" -> name = getNameEn();
            default -> name = getNameUz();
        }
    }

    /*
     * code starting => from: INFO | WARN | ERROR | SUCCESS
     * format: ${form}0000
     * */
}
