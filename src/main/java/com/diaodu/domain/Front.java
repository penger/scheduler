package com.diaodu.domain;

public class Front {

	private String id;
	private String href;
	private String name;
	private String parent_id;
	private String sort;
	private String is_show;
	private String permission;
	private String pId;

	public String getpId() {
		return parent_id;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getParent_id() {
		return parent_id;
	}

	public void setParent_id(String parent_id) {
		this.pId = parent_id;
		this.parent_id = parent_id;
	}

	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}

	public String getIs_show() {
		return is_show;
	}

	public void setIs_show(String is_show) {
		this.is_show = is_show;
	}

	public String getPermission() {
		return permission;
	}

	public void setPermission(String permission) {
		this.permission = permission;
	}

	@Override
	public String toString() {
		return "Front{" +
				"id='" + id + '\'' +
				", href='" + href + '\'' +
				", name='" + name + '\'' +
				", parent_id='" + parent_id + '\'' +
				", sort='" + sort + '\'' +
				", is_show='" + is_show + '\'' +
				", permission='" + permission + '\'' +
				", pId='" + pId + '\'' +
				'}';
	}
}
