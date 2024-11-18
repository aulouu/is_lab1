package itmo.aulouu.is_lab1.controller;

import itmo.aulouu.is_lab1.dto.coordinates.*;
import itmo.aulouu.is_lab1.service.CoordinatesService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/coordinates")
@RequiredArgsConstructor
public class CoordinatesController {
   private final CoordinatesService coordinatesService;

   @GetMapping
   public List<CoordinatesDTO> getCoordinates(@RequestParam int from, @RequestParam int size) {
      return coordinatesService.getCoordinates(from, size);
   }

   @PostMapping
   public CoordinatesDTO createCoordinate(@RequestBody @Valid CreateCoordinatesDTO createCoordinatesDTO,
         HttpServletRequest request) {
      return coordinatesService.createCoordinate(createCoordinatesDTO, request);
   }

   @PatchMapping("/{coordinatesId}")
   public CoordinatesDTO alterCoordinate(@PathVariable Long coordinatesId,
         @RequestBody @Valid AlterCoordinatesDTO alterCoordinatesDTO, HttpServletRequest request) {
      return coordinatesService.alterCoordinate(coordinatesId, alterCoordinatesDTO, request);
   }

   @DeleteMapping("/{coordinatesId}")
   public void deleteCoordinates(@PathVariable Long coordinatesId, HttpServletRequest request) {
      coordinatesService.deleteCoordinates(coordinatesId, request);
   }

}