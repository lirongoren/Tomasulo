package registers;

public class Register {

	protected String name;
	protected String tag;
	protected Status status;

	Register() {
		this.name = "";
		this.tag = "";
		this.status = Status.VALUE;
	}

	public String getName() {
		return name;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
		this.status = Status.TAG;
	}
	
	public Status getStatus() {
		return this.status;
	}
	
	public void setStatus(Status status) {
		this.status = status;
	}
	
	public enum Status {
		VALUE("value"),
		TAG("tag");

		@SuppressWarnings("unused")
		private String status;

		private Status(String status) {
			this.status = status;
		}
	}
}
