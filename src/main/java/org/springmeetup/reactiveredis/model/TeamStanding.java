package org.springmeetup.reactiveredis.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(of="teamId")
@AllArgsConstructor
@NoArgsConstructor
public class TeamStanding  {

    private String teamId;

    private String value;

    private String teamName;

    private String position;

    private String played;

    private String won;

    private String draw;

    private String lost;

    private String goalsScored;

    private String goalsAgainst;

    private String points;

    private String wpg;

    private String gb;

    private String description;

    private String teamForm;

}
