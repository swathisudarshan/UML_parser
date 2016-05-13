import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import net.sourceforge.plantuml.SourceStringReader;
public class UMLgenerate {
	public  void umlCreator(String source,String destination) {
		
		OutputStream image = null;
		try {
			image = new FileOutputStream(destination);
			System.out.println("Package");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			SourceStringReader reader = new SourceStringReader(source);
		
		try {
			reader.generateImage(image);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// Return a null string if no generation
	}
}