/**
 * 
 */
package com.inditex.selectionprocess.model.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * 
 */
@Data
@Entity
@Table(name="PHOTOS")
public class EPhoto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6548644790804877479L;
	@Id
	@GeneratedValue(strategy=GenerationType.UUID)
	private String id;
	private String name;
	private String type;
	@Column(nullable=true)
	private String description;
}
