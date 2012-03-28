package models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.QueryResultIterable;
import com.googlecode.objectify.Query;

import play.cache.Cache;
import play.data.validation.Required;
import play.modules.objectify.Datastore;
import play.modules.objectify.ObjectifyModel;
import play.modules.objectify.ObjectifyService;

public class User extends ObjectifyModel implements Serializable {

	@Id
	public Long id;

	@Required
	public String name;
	public boolean connected = false;

	public static User findByName(String name) {

		User user = null;
			try {
				user = Datastore.get(User.class, name);
			} catch (EntityNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return user;
	}
	
	public void save() {
		Datastore.put(this);
	}

	public void delete() {
		Datastore.delete(this);
	}
}
