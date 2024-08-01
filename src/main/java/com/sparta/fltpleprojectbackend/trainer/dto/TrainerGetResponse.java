package com.sparta.fltpleprojectbackend.trainer.dto;

import com.sparta.fltpleprojectbackend.trainer.entity.Trainer;
import lombok.Getter;

@Getter
public class TrainerGetResponse {
  //TODO: location + price 추가해야함
  private Long id;
  private String name;
  private String trainerPicture;

  public TrainerGetResponse(Trainer trainer) {
    this.id = trainer.getTrainerId();
    this.name = trainer.getTrainerName();
    this.trainerPicture = trainer.getTrainerPicture();
  }
}
