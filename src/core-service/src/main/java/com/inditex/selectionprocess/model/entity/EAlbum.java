/**
 * 
 */
package com.inditex.selectionprocess.model.entity;

import java.io.Serializable;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * 
 */
@Data
@Entity
@Table(name = "ALBUMS")
public class EAlbum implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5379224802469484823L;

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private String id;
	@Column(nullable = false)
	private String name;
	@Column(nullable = true)
	private String description;
	@OneToMany
	@JoinColumn(name = "ALBUM_ID", referencedColumnName = "ID")
	private List<EPhoto> photos;
}
