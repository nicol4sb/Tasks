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

import play.cache.Cache;
import play.data.validation.Required;
import play.modules.objectify.Datastore;
import play.modules.objectify.ObjectifyModel;
import play.modules.objectify.ObjectifyService;

public class Task extends ObjectifyModel implements Serializable {

	private static final String TASKS_MAP = "tasksMap";

	@Id
	public Long id;

	public Boolean starred;
	public Boolean done;
	public Double budget;

	@Required
	public String title;
	@Required
	public String content;
	public Date createdAt;
	public Date author;

	public Task() {
		this.createdAt = new Date();
		starred = false;
		done = false;
		budget = 0.0;
	}

	public static Collection<Task> findAll() {

		// check the "all tasks" cache
		Map<Long, Task> tasksMap = Cache.get(TASKS_MAP, Map.class);

		if (tasksMap == null) {
			
			System.out.println("Populating memcache");

			// create the object, populate it, and update the cache map
			tasksMap = new TreeMap<Long, Task>();

			QueryResultIterable<Task> q = Datastore.query(Task.class)
					.order("done").order("-starred").order("title").fetch();
			for (Task task : q) {
				tasksMap.put(task.id, task);
			}

			Cache.set(TASKS_MAP, tasksMap);
		}

		return tasksMap.values();
	}

	public static Task findById(Long id) {

		Task task = null;

		Map<Long, Task> tasksMap = Cache.get(TASKS_MAP, Map.class);

		if (tasksMap == null) {
			tasksMap = new TreeMap<Long, Task>();
		}else{
			System.out.println("Reading taskmap from cache.");
		}

		task = tasksMap.get(id);

		// if the task is not in the cache, retrieve it from the datastore and
		// cache it
		if (task == null) {
			try {
				task = Datastore.get(Task.class, id);
				tasksMap.put(id, task);
				Cache.set(TASKS_MAP, tasksMap);
			} catch (EntityNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			System.out.println("Reading task from cache.");
		}
		
		return task;
	}

	public void save() {

		// persist the task
		Datastore.put(this);

		// Update the cache (think reordering) :
		Cache.delete(TASKS_MAP);
		Task.findAll(); // trigger the repopulation
		System.out.println("Cache cleared and reloaded after save.");
	}

	public void delete() {

		// update the persistence
		Datastore.delete(this);

		// update the "all tasks" cache
		Cache.get(TASKS_MAP, Map.class).remove(this.id);
	}

}
