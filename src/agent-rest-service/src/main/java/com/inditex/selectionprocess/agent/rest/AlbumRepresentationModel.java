/**
 * 
 */
package com.inditex.selectionprocess.agent.rest;

import java.util.List;

import org.springframework.hateoas.RepresentationModel;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class AlbumRepresentationModel extends RepresentationModel<AlbumRepresentationModel> {
	private String id;
	private String name;
	private String description;
	private List<String> photos;
}
