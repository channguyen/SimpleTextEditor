import java.io.IOException;

public class PdfThread extends Thread {
	private String prog;
	private String fileName;

	public PdfThread(String prog, String fileName) {
		this.prog = prog;
		this.fileName = fileName;
	}

	public void run() {
		ProcessBuilder pb = new ProcessBuilder(prog, fileName);
		try {
			Process p = pb.start();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}