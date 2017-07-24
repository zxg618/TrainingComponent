package trainingcomponent.io;

import java.util.ArrayList;

public class TypeRecord {
	private String type;
	private String typeCodeStr;
	private String typeUrl;
	private String parentTypeUrl;
	private ArrayList<String> childrenTypeUrls = new ArrayList<String>();
	

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTypeUrl() {
		return typeUrl;
	}

	public void setTypeUrl(String typeUrl) {
		this.typeUrl = typeUrl;
	}

	public String getParentTypeUrl() {
		return parentTypeUrl;
	}

	public void setParentTypeUrl(String parentTypeUrl) {
		this.parentTypeUrl = parentTypeUrl;
	}

	public ArrayList<String> getChildrenTypeUrls() {
		return childrenTypeUrls;
	}

	public void setChildrenTypeUrls(ArrayList<String> childrenTypeUrls) {
		this.childrenTypeUrls = childrenTypeUrls;
	}

	public String getTypeCodeStr() {
		return typeCodeStr;
	}

	public void setTypeCodeStr(String typeCodeStr) {
		this.typeCodeStr = typeCodeStr;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((childrenTypeUrls == null) ? 0 : childrenTypeUrls.hashCode());
		result = prime * result + ((parentTypeUrl == null) ? 0 : parentTypeUrl.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + ((typeCodeStr == null) ? 0 : typeCodeStr.hashCode());
		result = prime * result + ((typeUrl == null) ? 0 : typeUrl.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TypeRecord other = (TypeRecord) obj;
		if (childrenTypeUrls == null) {
			if (other.childrenTypeUrls != null)
				return false;
		} else if (!childrenTypeUrls.equals(other.childrenTypeUrls))
			return false;
		if (parentTypeUrl == null) {
			if (other.parentTypeUrl != null)
				return false;
		} else if (!parentTypeUrl.equals(other.parentTypeUrl))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		if (typeCodeStr == null) {
			if (other.typeCodeStr != null)
				return false;
		} else if (!typeCodeStr.equals(other.typeCodeStr))
			return false;
		if (typeUrl == null) {
			if (other.typeUrl != null)
				return false;
		} else if (!typeUrl.equals(other.typeUrl))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "TypeRecord [type=" + type + ", typeCodeStr=" + typeCodeStr + "]";
	}

	public String toFullString() {
		return "TypeRecord [type=" + type + ", typeCodeStr=" + typeCodeStr + ", typeUrl=" + typeUrl + ", parentTypeUrl="
				+ parentTypeUrl + ", childrenTypeUrls=" + childrenTypeUrls + "]";
	}

	

}
