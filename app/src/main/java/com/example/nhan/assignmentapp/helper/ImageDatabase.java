package com.example.nhan.assignmentapp.helper;

import android.content.Context;
import android.util.LongSparseArray;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ImageDatabase implements IDataStore {
    private static String DATABASE_NAME;
    private Context context;
    private LongSparseArray<Image> imageList;

    public ImageDatabase() {
        imageList = new LongSparseArray<>();
    }

    public void init(Context context, String db) {
        this.context = context;
        DATABASE_NAME = db;
    }

    public void addImage(Image img) throws IOException {
        imageList.put(img.getId(), img);
        saveState();
    }

    public Image getImage(long id) {
        return imageList.get(id);
    }

    public LongSparseArray<Image> getImageList() {
        return imageList;
    }

    LongSparseArray<Image> getDatabase() {
        return imageList;
    }

    public List<Image> getImages() {
        List<Image> images = new ArrayList<>();
        for (int i = 0; i < imageList.size(); i++) {
            long key = imageList.keyAt(i);
            images.add(imageList.get(key));
        }
        return images;
    }

    public List<Image> getImagesByDate(Date start, Date end) {
        List<Image> images = new ArrayList<>();
        for (int i = 0; i < imageList.size(); i++) {
            long key = imageList.keyAt(i);
            if (imageList.get(key).getTimestamp().compareTo(start) * imageList.get(key).getTimestamp().compareTo(end) < 0) {
                images.add(imageList.get(key));
            }
        }
        return images;
    }

    public List<Image> getImagesByCaption(String caption) {
        List<Image> images = new ArrayList<>();
        for (int i = 0; i < imageList.size(); i++) {
            long key = imageList.keyAt(i);
            if (imageList.get(key).getCaption().toLowerCase().contains(caption.toLowerCase())) {
                images.add(imageList.get(key));
            }
        }
        return images;
    }

    public List<Image> getImagesByDateAndCaption(Date start, Date end, String caption) {
        List<Image> images = new ArrayList<>();
        for (int i = 0; i < imageList.size(); i++) {
            long key = imageList.keyAt(i);
            if (imageList.get(key).getTimestamp().compareTo(start) * imageList.get(key).getTimestamp().compareTo(end) < 0 && imageList.get(key).getCaption().toLowerCase().contains(caption.toLowerCase())) {
                images.add(imageList.get(key));
            }
        }
        return images;
    }

    public void updateImageCaption(long id, String caption) throws IOException {
        if (imageList.indexOfKey(id) < 0) throw new NullPointerException("That Image ID does not exist in the database.");
        imageList.get(id).setCaption(caption);
        saveState();
    }

    public void clear() throws IOException {
        imageList.clear();
        saveState();
    }

    @Override
    public void saveState() throws IOException {
        FileOutputStream fos = context.openFileOutput(DATABASE_NAME, Context.MODE_PRIVATE);
        ObjectOutputStream os = new ObjectOutputStream(fos);
        os.writeObject(getImages());
        os.close();
        fos.close();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void loadState() throws IOException, ClassNotFoundException {
        FileInputStream fis = context.openFileInput(DATABASE_NAME);
        ObjectInputStream is = new ObjectInputStream(fis);
        List<Image> images = (ArrayList<Image>) is.readObject();
        for (Image img : images) {
            File file = new File(img.getFilePath());
            if (file.exists()) {
                imageList.append(img.getId(), img);
            }
        }
        is.close();
        fis.close();
    }
}