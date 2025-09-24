//package app.ruzi.entity;
//import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
//import jakarta.persistence.*;
//import lombok.*;
//
//@Entity
//@Table(name = "suppliers", schema = "ruzi")
//@Getter
//@Setter
//@NoArgsConstructor
//@AllArgsConstructor
//@Builder
//@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
//public class Supplier {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @Column(nullable = false, length = 150)
//    private String name;
//
//    private String phone;
//    private String email;
//    private String contactPerson;
//    private String address;
//    private String inn;        // STIR / INN
//    private String bankAccount;
//}
