package com.bomboverk.boat.SystemInformation;

import android.app.ActivityManager;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.system.ErrnoException;
import android.system.Os;
import android.system.StructStatVfs;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.bomboverk.boat.ItensAdapter.Itens;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import static android.content.Context.ACTIVITY_SERVICE;

public class SystemInformationHelper {
    private Context context;
    private ActivityManager activityManager;
    private ActivityManager.MemoryInfo memoryInfo;

    public SystemInformationHelper(Context context) {
        this.context = context;

        //Load memory infos
        activityManager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
    }

    //PEGA MEMORIA RAM SENDO USADA
    public int getUsedRamMemorySize() {
        long usedMemory = memoryInfo.availMem / 1048576L;
        return (int) usedMemory;
        //memoryInfo.threshold LIMITE
        //memoryInfo.availMem ATUAL
        //memoryInfo.totalMem MAXIMO
    }

    //PEGA MEMORIA RAM TOTAL
    public int getTotalRamMemorySize() {
        long totalMemory = memoryInfo.totalMem / 1048576L;
        return (int) totalMemory;
    }

    public long getTotalMemorySize(String way) {
        Uri docTreeUri = DocumentsContract.buildDocumentUriUsingTree(Uri.parse(way), DocumentsContract.getTreeDocumentId(Uri.parse(way)));
        try {
            ParcelFileDescriptor pfd = context.getContentResolver().openFileDescriptor(docTreeUri, "r");
            StructStatVfs stats = Os.fstatvfs(pfd.getFileDescriptor());

            long totalMemory = stats.f_blocks * stats.f_bsize;
            return totalMemory / (1024 * 1024);
            //Log.i("ded", "block_size=" + stats.f_bsize + ", num_of_blocks=" + stats.f_blocks);
            //Log.i("ded", "free space in Megabytes:" + stats.f_bavail * stats.f_bsize / 1024 / 1024);
        } catch (FileNotFoundException | ErrnoException e) {
            return 0;
        }
    }

    public long getUsedMemorySize(String way) {
        Uri docTreeUri = DocumentsContract.buildDocumentUriUsingTree(Uri.parse(way), DocumentsContract.getTreeDocumentId(Uri.parse(way)));
        try {
            ParcelFileDescriptor pfd = context.getContentResolver().openFileDescriptor(docTreeUri, "r");
            StructStatVfs stats = Os.fstatvfs(pfd.getFileDescriptor());

            long totalMemory = stats.f_blocks * stats.f_bsize;
            long disponible = stats.f_bavail * stats.f_bsize;
            long total = totalMemory - disponible;
            return total / (1024 * 1024);

        } catch (FileNotFoundException | ErrnoException e) {
            return 0;
        }
    }

    public ArrayList<Itens> getSongs() {
        ArrayList<Itens> itens = new ArrayList<>();

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + "!=0";
        String orderBy = MediaStore.Audio.Media.DISPLAY_NAME + " ASC";
        Cursor cursor = context.getContentResolver().query(uri, null, selection, null, orderBy);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    String nome = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
                    long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
                    Uri url = Uri.withAppendedPath(uri, "" + id);

                    itens.add(new Itens(nome, url.toString(), "", "", 0, null));

                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        return itens;
    }

    public ArrayList<Itens> getVideos() {
        ArrayList<Itens> itens = new ArrayList<>();

        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    String nome = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME));
                    long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media._ID));
                    Uri url = Uri.withAppendedPath(uri, "" + id);

                    itens.add(new Itens(nome, url.toString(), "", "", 0, null));

                } while (cursor.moveToNext());
                cursor.close();
            }
        }
        return itens;
    }

    public ArrayList<Itens> getImages() {
        ArrayList<Itens> itens = new ArrayList<>();

        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    String nome = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
                    long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media._ID));
                    Uri url = Uri.withAppendedPath(uri, "" + id);

                    itens.add(new Itens(nome, url.toString(), "", "", 0, null));

                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return itens;
    }

    /*public ArrayList<Itens> getApps() {
        ArrayList<Itens> itens = new ArrayList<>();

        String[] mimes = new String[]{MimeTypeMap.getSingleton().getMimeTypeFromExtension("%.pdf")};
        String[] selectionArgsPdf = new String[]{mimes[0]};

        Uri uri = MediaStore.Files.getContentUri("external");
        String selection = MediaStore.Files.FileColumns.MIME_TYPE + "=?";

        Cursor cursor = context.getContentResolver().query(uri, null, selection, selectionArgsPdf, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    String nome = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DISPLAY_NAME));
                    long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Files.FileColumns._ID));
                    Uri url = Uri.withAppendedPath(uri, "" + id);

                    Log.i("edfe", ""+nome);

                    itens.add(new Itens(nome, url.toString(), "", "", 0, null));

                } while (cursor.moveToNext());

            }
            cursor.close();
        }

        return itens;
    }*/



    /*VERIFICA MEMORIA INTERNA USADA
    public long getAvailableInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSizeLong();
        long availableBlocks = stat.getAvailableBlocksLong();
        long totalBlocks = stat.getBlockCountLong();
        long totals = totalBlocks * blockSize;
        long totalavaible = availableBlocks * blockSize;
        long total = totals - totalavaible;
        return total / (1024 * 1024);
    }

    //VERIFICA TOTAL DE MEORIA INTERNA
    public long getTotalInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSizeLong();
        long totalBlocks = stat.getBlockCountLong();
        long total = totalBlocks * blockSize;
        return total / (1024 * 1024);
    }

    /*
    //PEGA MEMORIA SENDO USADA
    public long getAvailableExternalMemorySize(String uriExternal) {
        DocumentFile pickedDir = DocumentFile.fromTreeUri(context, Uri.parse(uriExternal));
        long a = getFolderSize(pickedDir);
        return a / (1024 * 1024);
    }

    PEGA TOTAL DE ARMAZENAMENTO
    public long getTotalExternalMemorySize(String uriExternal) {
        DocumentFile pickedDir = DocumentFile.fromTreeUri(context, Uri.parse(uriExternal));
        File path = new File(removableStoragePath);
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSizeLong();
        long totalBlocks = stat.getBlockCountLong();
        long total = totalBlocks * blockSize;
        return total / (1024 * 1024);
    }*/

    /*public long getImagesSize() {
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                if (getExternalDisk()) {
                    do {
                        String url = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));

                        if (!url.contains(removableStoragePath)) {
                            imagensSize += getFolderSize(new File(url)) / (1024 * 1024);
                        }
                    } while (cursor.moveToNext());
                } else {
                    do {
                        String url = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                        imagensSize += getFolderSize(new File(url)) / (1024 * 1024);
                    } while (cursor.moveToNext());
                }
            }
            cursor.close();
        }
        return imagensSize;
    }

    public long getVideosSize() {
        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                if (getExternalDisk()) {
                    do {
                        String url = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));

                        if (!url.contains(removableStoragePath)) {
                            videosSize += getFolderSize(new File(url)) / (1024 * 1024);
                        }
                    } while (cursor.moveToNext());
                } else {
                    do {
                        String url = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
                        videosSize += getFolderSize(new File(url)) / (1024 * 1024);
                    } while (cursor.moveToNext());
                }
            }
            cursor.close();
        }
        return videosSize;
    }*/

    /*
    //VERIFICA MEMORIA INTERNA USADA
    public long getAvailableInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSizeLong();
        long availableBlocks = stat.getAvailableBlocksLong();
        long total = availableBlocks * blockSize;
        return total / (1024 * 1024);
    }
    */

        /*public long getDocuments() {

        String[] mimes = new String[]{MimeTypeMap.getSingleton().getMimeTypeFromExtension("png"),
                MimeTypeMap.getSingleton().getMimeTypeFromExtension("mp3"),
                MimeTypeMap.getSingleton().getMimeTypeFromExtension("jpeg"),
                MimeTypeMap.getSingleton().getMimeTypeFromExtension("mp4")};

        String[] selectionArgsPdf = new String[]{ mimes[0], mimes[1], mimes[2], mimes[3]};

        Uri uri = MediaStore.Files.getContentUri("external");
        String selection = MediaStore.Files.FileColumns.MIME_TYPE + " IS NOT ? AND " + MediaStore.Files.FileColumns.MIME_TYPE + " IS NOT ? AND "
                + MediaStore.Files.FileColumns.MIME_TYPE + " IS NOT ? AND " + MediaStore.Files.FileColumns.MIME_TYPE + " IS NOT ?";

        Cursor cursor = context.getContentResolver().query(uri, null, selection, selectionArgsPdf, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                if (getExternalDisk()) {
                    do {
                        String url = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA));

                        if (!url.contains(removableStoragePath) && !url.contains("storage/emulated/0/Android/data")) {
                            documentsSize += getFolderSize(new File(url)) / (1024 * 1024);
                        }
                    } while (cursor.moveToNext());
                } else {
                    do {
                        String url = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA));

                        if(!url.contains("storage/emulated/0/Android/data")){
                            documentsSize += getFolderSize(new File(url)) / (1024 * 1024);
                        }
                    } while (cursor.moveToNext());
                }
            }
            cursor.close();
        }

        return documentsSize;
    }*/

}
