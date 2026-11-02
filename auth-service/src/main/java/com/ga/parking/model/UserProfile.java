package com.ga.parking.model;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class UserProfile {

    private String firstName;

    private String lastName;

    private String profileDescription;
}