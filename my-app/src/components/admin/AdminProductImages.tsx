import React, { useState, useEffect, useCallback } from "react";
import { Trash2, Star, Plus, ArrowLeft, ArrowRight, Loader2 } from "lucide-react";
import { adminApi } from "../../api/admin.api";
import type { ProductImage } from "../../types";
import toast from "react-hot-toast";

interface AdminProductImagesProps {
  productId: number;
}

const AdminProductImages: React.FC<AdminProductImagesProps> = ({ productId }) => {
  const [images, setImages] = useState<ProductImage[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [isUploading, setIsUploading] = useState(false);
  const [newImageUrl, setNewImageUrl] = useState("");

  const fetchImages = useCallback(async () => {
    try {
      setIsLoading(true);
      const data = await adminApi.listImages(productId);
      setImages(data);
    } catch (error) {
      toast.error("Failed to load product images");
    } finally {
      setIsLoading(false);
    }
  }, [productId]);

  useEffect(() => {
    fetchImages();
  }, [fetchImages]);

  const handleAddImage = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!newImageUrl.trim()) return;
    try {
      setIsUploading(true);
      await adminApi.addImage(productId, { url: newImageUrl });
      toast.success("Image added successfully");
      setNewImageUrl("");
      fetchImages();
    } catch (error) {
      toast.error("Failed to add image");
    } finally {
      setIsUploading(false);
    }
  };

  const handleDeleteImage = async (imageId: number) => {
    if (!window.confirm("Are you sure you want to delete this image?")) return;
    try {
      await adminApi.deleteImage(productId, imageId);
      toast.success("Image deleted");
      fetchImages();
    } catch (error) {
      toast.error("Failed to delete image");
    }
  };

  const handleSetPrimary = async (imageId: number) => {
    try {
      await adminApi.setPrimaryImage(productId, imageId);
      toast.success("Primary image updated");
      fetchImages();
    } catch (error) {
      toast.error("Failed to set primary image");
    }
  };

  const handleMoveImage = async (index: number, direction: 'left' | 'right') => {
    if (direction === 'left' && index === 0) return;
    if (direction === 'right' && index === images.length - 1) return;

    const newImages = [...images];
    const swapIndex = direction === 'left' ? index - 1 : index + 1;
    
    // Swap
    [newImages[index], newImages[swapIndex]] = [newImages[swapIndex], newImages[index]];
    
    setImages(newImages); // Optimistic UI update

    const newOrderIds = newImages.map(img => img.id);
    try {
      await adminApi.reorderImages(productId, newOrderIds);
    } catch (error) {
      toast.error("Failed to reorder images");
      fetchImages(); // Revert on failure
    }
  };

  if (isLoading) {
    return (
      <div className="flex items-center justify-center py-8">
        <Loader2 className="w-6 h-6 animate-spin text-neutral-400" />
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* Add new image form */}
      <form onSubmit={handleAddImage} className="flex gap-3">
        <input
          type="url"
          required
          placeholder="Paste image URL here..."
          value={newImageUrl}
          onChange={(e) => setNewImageUrl(e.target.value)}
          className="flex-1 px-4 py-2 rounded-lg border border-neutral-300 dark:border-neutral-700 bg-transparent text-sm focus:ring-2 focus:ring-black dark:focus:ring-white outline-none"
        />
        <button
          type="submit"
          disabled={isUploading}
          className="inline-flex items-center gap-2 px-4 py-2 bg-black text-white dark:bg-white dark:text-black rounded-lg text-sm font-medium hover:bg-neutral-800 dark:hover:bg-neutral-200 transition-colors disabled:opacity-50 shrink-0"
        >
          {isUploading ? <Loader2 className="w-4 h-4 animate-spin" /> : <Plus className="w-4 h-4" />}
          Add
        </button>
      </form>

      {/* Image Gallery */}
      {images.length === 0 ? (
        <div className="text-center py-8 px-4 border border-dashed border-neutral-300 dark:border-neutral-700 rounded-lg">
          <p className="text-sm text-neutral-500">No images added yet.</p>
        </div>
      ) : (
        <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-2 xl:grid-cols-3 gap-4">
          {images.map((img, index) => (
            <div key={img.id} className="group relative aspect-square rounded-lg border border-neutral-200 dark:border-neutral-800 overflow-hidden bg-neutral-50 dark:bg-neutral-900/50">
              <img src={img.url} alt={`Product view ${index + 1}`} className="w-full h-full object-cover" />
              
              {/* Primary Badge */}
              {img.isPrimary && (
                <div className="absolute top-2 left-2 px-2 py-1 bg-yellow-500 text-white text-[10px] font-bold uppercase tracking-widest rounded flex items-center gap-1 shadow-sm">
                  <Star className="w-3 h-3 fill-current" />
                  Primary
                </div>
              )}

              {/* Hover Overlay Actions */}
              <div className="absolute inset-0 bg-black/60 opacity-0 group-hover:opacity-100 transition-opacity flex flex-col justify-between p-2">
                <div className="flex justify-end gap-1">
                  {!img.isPrimary && (
                    <button
                      onClick={() => handleSetPrimary(img.id)}
                      className="p-1.5 bg-white/20 hover:bg-yellow-500 text-white rounded backdrop-blur-sm transition-colors"
                      title="Set as Primary"
                    >
                      <Star className="w-4 h-4" />
                    </button>
                  )}
                  <button
                    onClick={() => handleDeleteImage(img.id)}
                    className="p-1.5 bg-white/20 hover:bg-red-500 text-white rounded backdrop-blur-sm transition-colors"
                    title="Delete Image"
                  >
                    <Trash2 className="w-4 h-4" />
                  </button>
                </div>
                
                <div className="flex justify-center gap-2">
                  <button
                    onClick={() => handleMoveImage(index, 'left')}
                    disabled={index === 0}
                    className="p-1.5 bg-white/20 hover:bg-white text-white hover:text-black rounded backdrop-blur-sm transition-colors disabled:opacity-30 disabled:hover:bg-white/20 disabled:hover:text-white"
                  >
                    <ArrowLeft className="w-4 h-4" />
                  </button>
                  <button
                    onClick={() => handleMoveImage(index, 'right')}
                    disabled={index === images.length - 1}
                    className="p-1.5 bg-white/20 hover:bg-white text-white hover:text-black rounded backdrop-blur-sm transition-colors disabled:opacity-30 disabled:hover:bg-white/20 disabled:hover:text-white"
                  >
                    <ArrowRight className="w-4 h-4" />
                  </button>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default AdminProductImages;
