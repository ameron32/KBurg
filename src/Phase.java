
public class Phase {
	private String name;
	private int ordinal;
	private boolean isProductive;
	private boolean isAid;
	private boolean usesAid;
	private boolean isReward;
	private boolean isEnvoy;
	private boolean isRecruit;
	private boolean isBattle;
	
	
	
	private Phase(String name, int ordinal, boolean isProductive, boolean isAid, boolean usesAid,
			boolean isReward, boolean isEnvoy, boolean isRecruit,
			boolean isBattle) {
		super();
		this.name = name;
		this.ordinal = ordinal;
		this.isProductive = isProductive;
		this.isAid = isAid;
		this.usesAid = usesAid;
		this.isReward = isReward;
		this.isEnvoy = isEnvoy;
		this.isRecruit = isRecruit;
		this.isBattle = isBattle;
	}

	public String getName() {
		return name;
	}

	public int getOrdinal() {
		return ordinal;
	}

	public boolean isProductive() {
		return isProductive;
	}

	public boolean gainsAid() {
		return isAid;
	}
	
	public boolean usesAid() {
		return usesAid;
	}

	public boolean isReward() {
		return isReward;
	}

	public boolean isEnvoy() {
		return isEnvoy;
	}

	public boolean isRecruit() {
		return isRecruit;
	}

	public boolean isBattle() {
		return isBattle;
	}
	
	
	
	public static class Builder {
		String name;
		int ordinal;
		boolean isProductive;
		boolean isAid;
		boolean usesAid;
		boolean isReward;
		boolean isEnvoy;
		boolean isRecruit;
		boolean isBattle;
		
		public static Builder of() {
			return new Builder();
		}
		
		public Builder name(String name) {
			this.name = name;
			return this;
		}
		
		public Builder ordinal(int position) {
			this.ordinal = position;
			return this;
		}
		
		public Builder productive() {
			this.isProductive = true;
			return this;
		}
		
		public Builder gainsAid() {
			this.isAid = true;
			return this;
		}
		
		public Builder usesAid() {
			this.usesAid = true;
			return this;
		}
		
		public Builder reward() {
			this.isReward = true;
			return this;
		}
		
		public Builder envoy() {
			this.isEnvoy = true;
			return this;
		}
		
		public Builder recruit() {
			this.isRecruit = true;
			return this;
		}
		
		public Builder battle() {
			this.isBattle = true;
			return this;
		}
		
		public Phase make() {
			return new Phase(name, ordinal, isProductive, isAid, usesAid, isReward, isEnvoy, isRecruit, isBattle);
		}
	}
}
