package itmo.aulouu.is_lab1.dto.location;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LocationDTO {
   private Long id;
   private double x;
   private Integer y;
   private double z;
   private String name;
   private Boolean adminCanModify;
   private Long userId;
}