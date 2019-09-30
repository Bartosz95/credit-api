package bolo.spring.creditapplication.domain;

import bolo.spring.creditapplication.controllers.Views;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Customer {

    long id;

    @JsonView(Views.Public.class)
    String firstName;

    @JsonView(Views.Public.class)
    String lastName;

    @JsonView(Views.Public.class)
    long personalId;
}
