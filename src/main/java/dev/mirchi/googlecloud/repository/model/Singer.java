package dev.mirchi.googlecloud.repository.model;

import com.google.cloud.spring.data.spanner.core.mapping.Column;
import com.google.cloud.spring.data.spanner.core.mapping.PrimaryKey;
import com.google.cloud.spring.data.spanner.core.mapping.Table;

import java.time.LocalDate;

@Table(name = "Singers")
public class Singer {

    @PrimaryKey
    @Column(name = "SingerId") private Long singerId;

    @Column(name = "FirstName") private String firstName;

    @Column(name = "LastName") private String lastName;

    @Column(name = "BirthDate") private LocalDate birthDate;

    public Long getSingerId() {
        return singerId;
    }

    public void setSingerId(Long singerId) {
        this.singerId = singerId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }
}
