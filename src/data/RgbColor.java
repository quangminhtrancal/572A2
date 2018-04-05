package data;


public class RgbColor{
	
	private int r;
	private int g;
	private int b;
	
	public RgbColor(int r, int g, int b) {
		if(!(validate(r) && validate(g) && validate(b))){
			throw new IllegalArgumentException();
		}
		this.r = r;
		this.g = g;
		this.b = b;
	}

	public int getR() {
		return r;
	}

	public void setR(int r) {
		if(!validate(r)){
			throw new IllegalArgumentException();
		}
		this.r = r;
	}

	public int getG() {
		return g;
	}

	public void setG(int g) {
		if(!validate(g)){
			throw new IllegalArgumentException();
		}
		this.g = g;
	}

	public int getB() {
		return b;
	}

	public void setB(int b) {
		if(!validate(b)){
			throw new IllegalArgumentException();
		}
		this.b = b;
	}
	
	private boolean validate(int value){
		if(value < 0 || value > 255){
			return false;
		}
		return true;
	}
	
	public static RgbColor[] getRainbow(int numColors, int darkness){
		
		int numUnits = (int)Math.ceil((-3 + Math.sqrt(1 + 8 * numColors)) / 2);
		int unit = (int)Math.ceil((double)darkness / numUnits);
			
		RgbColor[] colors = new RgbColor[numColors];
		int index = 0;
		
		for(int rUnits = 0; rUnits <= numUnits; rUnits++){
			for(int gUnits = 0; gUnits <= numUnits - rUnits; gUnits++){
				
				int bUnits = numUnits - rUnits - gUnits;
				
				int rVal = Math.min(rUnits * unit, 255);
				int gVal = Math.min(gUnits * unit, 255);
				int bVal = Math.min(bUnits * unit, 255);
				
				colors[index++] = new RgbColor(rVal, gVal, bVal);
				if(index == numColors){
					return colors;
				}
			}
		}
		return colors;
		
	}

	@Override
	public String toString() {
		return "RGB(" + r + ", " + g + ", " + b + ")";
	}
	
	
}