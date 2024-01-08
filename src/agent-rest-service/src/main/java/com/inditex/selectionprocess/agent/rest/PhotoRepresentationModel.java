/**
 * 
 */
package com.inditex.selectionprocess.agent.rest;

import org.springframework.hateoas.RepresentationModel;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class PhotoRepresentationModel extends RepresentationModel<PhotoRepresentationModel> {
	private String id;
	private String name;
	private String description;
	private String type;
}
