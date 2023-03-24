package model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private int id;
    private String username;
    private String firstName;
    private String lastName;
    private String password;
    private String box;
    private ERole role;
    private EPersonStatus status;
}
