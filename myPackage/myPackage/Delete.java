package myPackage;

import java.io.File;

public class Delete {

	public Delete(String fileName){
	// A File object to represent the filename
		File f = new File(fileName);
	// Make sure the file or directory exists and isn't write protected
//    if (!f.exists())
//      throw new IllegalArgumentException(
//          "Delete: no such file or directory: " + fileName);

    if (!f.canWrite())
      throw new IllegalArgumentException("Delete: write protected: "
          + fileName);

    // If it is a directory, make sure it is empty
    if (f.isDirectory()) {
      String[] files = f.list();
      if (files.length > 0)
        throw new IllegalArgumentException(
            "Delete: directory not empty: " + fileName);
    }

    // Attempt to delete it
    boolean success = f.delete();

    if (!success)
      throw new IllegalArgumentException("Delete: deletion failed");
	}
}