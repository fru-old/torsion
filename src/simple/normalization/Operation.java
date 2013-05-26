package simple.normalization;

public class Operation {

	public final int location;
	public Operation(int location){
		this.location = location;
	}
	
	public String toString(){
		return "NOOP "+location;
	}
	
	public static class Jump extends Operation {
		
		public final int target;
		public Jump(int location, int target){
			super(location);
			this.target = target;
		}
		
		public boolean isForward(){
			return location < target;
		}
		
		public String toString(){
			return location+": Goto_" + (isForward()?"Forward":"Backward") + " " + target;
		}
		
		private Block block;
		public Block getBlock(){
			return block;
		}
		public void setBlock(Block block){
			this.block = block;
		}
	}
}
