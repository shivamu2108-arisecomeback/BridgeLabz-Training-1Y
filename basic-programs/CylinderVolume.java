import java.util.Scanner;
class CylinderVolume{
	public static void main(String[] args){
		Scanner sc = new Scanner(System.in);
		int radius = sc.nextInt();
		int height = sc.nextInt();
		double Volume = (3.14*radius*radius*height);
		System.out.println(Volume);
		
	}
	
}