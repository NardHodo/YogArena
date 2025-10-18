package com.example.yogarena;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.media.Image;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicYuvToRGB;
import android.renderscript.Type;
import androidx.camera.core.ImageProxy;
import java.nio.ByteBuffer;

public class YuvToRgbConverter {

    private final RenderScript rs;
    private final ScriptIntrinsicYuvToRGB scriptYuvToRgb;
    private int yuvLength = 0;
    private Allocation yuvAllocation;
    private Allocation rgbAllocation;

    public YuvToRgbConverter(Context context) {
        rs = RenderScript.create(context);
        scriptYuvToRgb = ScriptIntrinsicYuvToRGB.create(rs, Element.U8_4(rs));
    }

    public synchronized Bitmap yuvToRgb(ImageProxy imageProxy) {
        if (imageProxy.getFormat() != ImageFormat.YUV_420_888) {
            throw new IllegalArgumentException("Invalid image format");
        }

        byte[] yuvBytes = imageToNv21(imageProxy);

        // Lazily create the allocations to match the image size
        if (yuvAllocation == null || yuvLength != yuvBytes.length) {
            yuvLength = yuvBytes.length;
            Type.Builder yuvType = new Type.Builder(rs, Element.U8(rs)).setX(yuvLength);
            yuvAllocation = Allocation.createTyped(rs, yuvType.create(), Allocation.USAGE_SCRIPT);

            Type.Builder rgbType = new Type.Builder(rs, Element.RGBA_8888(rs))
                    .setX(imageProxy.getWidth())
                    .setY(imageProxy.getHeight());
            rgbAllocation = Allocation.createTyped(rs, rgbType.create(), Allocation.USAGE_SCRIPT);
        }

        // Copy the YUV data to the allocation and convert
        yuvAllocation.copyFrom(yuvBytes);
        scriptYuvToRgb.setInput(yuvAllocation);
        scriptYuvToRgb.forEach(rgbAllocation);

        // Copy the result to a bitmap
        Bitmap bitmap = Bitmap.createBitmap(imageProxy.getWidth(), imageProxy.getHeight(), Bitmap.Config.ARGB_8888);
        rgbAllocation.copyTo(bitmap);
        return bitmap;
    }

    /**
     * Helper function to convert an ImageProxy in YUV_420_888 format to a byte array in NV21 format.
     */
    private byte[] imageToNv21(ImageProxy image) {
        ImageProxy.PlaneProxy yPlane = image.getPlanes()[0];
        ImageProxy.PlaneProxy uPlane = image.getPlanes()[1];
        ImageProxy.PlaneProxy vPlane = image.getPlanes()[2];

        ByteBuffer yBuffer = yPlane.getBuffer();
        ByteBuffer uBuffer = uPlane.getBuffer();
        ByteBuffer vBuffer = vPlane.getBuffer();

        int ySize = yBuffer.remaining();
        int uSize = uBuffer.remaining();
        int vSize = vBuffer.remaining();

        byte[] nv21 = new byte[ySize + uSize + vSize];

        // U and V are swapped
        yBuffer.get(nv21, 0, ySize);
        vBuffer.get(nv21, ySize, vSize);
        uBuffer.get(nv21, ySize + vSize, uSize);

        return nv21;
    }
}


