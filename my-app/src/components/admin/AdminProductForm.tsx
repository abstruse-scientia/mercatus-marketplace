import React, { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { ArrowLeft, Save, Loader2 } from "lucide-react";
import { adminApi } from "../../api/admin.api";
import type { Category, CreateProductRequest, UpdateProductRequest } from "../../types";
import toast from "react-hot-toast";
import AdminProductImages from "./AdminProductImages";

const AdminProductForm = () => {
  const { id } = useParams<{ id: string }>();
  const isEditMode = Boolean(id);
  const navigate = useNavigate();

  const [categories, setCategories] = useState<Category[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [isSaving, setIsSaving] = useState(false);

  const [formData, setFormData] = useState({
    name: "",
    description: "",
    price: "",
    sku: "",
    slug: "",
    categoryId: "",
  });

  useEffect(() => {
    const init = async () => {
      try {
        setIsLoading(true);
        // Fetch categories (assuming not more than 100 for now, so size=100)
        const cats = await adminApi.listCategories(0, 100);
        setCategories(cats.content);

        if (isEditMode && id) {
          const product = await adminApi.getProduct(Number(id));
          setFormData({
            name: product.name,
            description: product.description || "",
            price: product.price.toString(),
            sku: product.sku,
            slug: product.slug,
            categoryId: product.categoryId.toString(),
          });
        }
      } catch (error) {
        toast.error("Failed to load product data");
        navigate("/admin/products");
      } finally {
        setIsLoading(false);
      }
    };
    init();
  }, [id, isEditMode, navigate]);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      setIsSaving(true);
      if (isEditMode && id) {
        const payload: UpdateProductRequest = {
          name: formData.name,
          description: formData.description,
          price: Number(formData.price),
          sku: formData.sku,
          slug: formData.slug,
          categoryId: Number(formData.categoryId),
        };
        await adminApi.updateProduct(Number(id), payload);
        toast.success("Product updated successfully");
      } else {
        const payload: CreateProductRequest = {
          name: formData.name,
          description: formData.description,
          price: Number(formData.price),
          sku: formData.sku,
          categoryId: Number(formData.categoryId),
        };
        await adminApi.createProduct(payload);
        toast.success("Product created successfully");
        navigate("/admin/products");
      }
    } catch (error) {
      toast.error(isEditMode ? "Failed to update product" : "Failed to create product");
    } finally {
      setIsSaving(false);
    }
  };

  if (isLoading) {
    return (
      <div className="flex items-center justify-center min-h-[400px]">
        <Loader2 className="w-8 h-8 animate-spin text-neutral-400" />
      </div>
    );
  }

  return (
    <div className="max-w-4xl mx-auto flex flex-col gap-8">
      {/* Header */}
      <div className="flex items-center gap-4">
        <button
          onClick={() => navigate("/admin/products")}
          className="p-2 -ml-2 text-neutral-500 hover:text-neutral-900 dark:text-neutral-400 dark:hover:text-white transition-colors"
        >
          <ArrowLeft className="w-5 h-5" />
        </button>
        <div>
          <h1 className="text-2xl font-bold text-neutral-900 dark:text-white">
            {isEditMode ? "Edit Product" : "Create Product"}
          </h1>
          <p className="text-sm text-neutral-600 dark:text-neutral-400 mt-1">
            {isEditMode ? "Update the details and images of your product." : "Add a new product to your inventory."}
          </p>
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        {/* Main Form */}
        <div className="lg:col-span-2 space-y-6">
          <form id="product-form" onSubmit={handleSubmit} className="bg-white dark:bg-neutral-900 border border-neutral-200 dark:border-neutral-800 rounded-xl p-6 shadow-sm space-y-6">
            <h2 className="text-lg font-semibold text-neutral-900 dark:text-white border-b border-neutral-200 dark:border-neutral-800 pb-4">
              Basic Details
            </h2>
            
            <div className="space-y-4">
              <div>
                <label className="block text-sm font-medium text-neutral-900 dark:text-neutral-100 mb-2">
                  Product Name
                </label>
                <input
                  type="text"
                  name="name"
                  required
                  value={formData.name}
                  onChange={handleChange}
                  placeholder="e.g. Vintage Leather Jacket"
                  className="w-full px-4 py-2.5 rounded-lg border border-neutral-300 dark:border-neutral-700 bg-transparent text-neutral-900 dark:text-white focus:ring-2 focus:ring-black dark:focus:ring-white outline-none transition-all"
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-neutral-900 dark:text-neutral-100 mb-2">
                  Description
                </label>
                <textarea
                  name="description"
                  rows={5}
                  value={formData.description}
                  onChange={handleChange}
                  placeholder="Describe your product..."
                  className="w-full px-4 py-2.5 rounded-lg border border-neutral-300 dark:border-neutral-700 bg-transparent text-neutral-900 dark:text-white focus:ring-2 focus:ring-black dark:focus:ring-white outline-none transition-all resize-y"
                />
              </div>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-6 pt-4">
              <div>
                <label className="block text-sm font-medium text-neutral-900 dark:text-neutral-100 mb-2">
                  Price ($)
                </label>
                <input
                  type="number"
                  name="price"
                  required
                  min="0"
                  step="0.01"
                  value={formData.price}
                  onChange={handleChange}
                  placeholder="0.00"
                  className="w-full px-4 py-2.5 rounded-lg border border-neutral-300 dark:border-neutral-700 bg-transparent text-neutral-900 dark:text-white focus:ring-2 focus:ring-black dark:focus:ring-white outline-none transition-all"
                />
              </div>
              
              <div>
                <label className="block text-sm font-medium text-neutral-900 dark:text-neutral-100 mb-2">
                  SKU
                </label>
                <input
                  type="text"
                  name="sku"
                  required
                  value={formData.sku}
                  onChange={handleChange}
                  placeholder="e.g. VINT-LEATH-01"
                  className="w-full px-4 py-2.5 rounded-lg border border-neutral-300 dark:border-neutral-700 bg-transparent text-neutral-900 dark:text-white focus:ring-2 focus:ring-black dark:focus:ring-white outline-none transition-all font-mono"
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-neutral-900 dark:text-neutral-100 mb-2">
                  Category
                </label>
                <select
                  name="categoryId"
                  required
                  value={formData.categoryId}
                  onChange={handleChange}
                  className="w-full px-4 py-2.5 rounded-lg border border-neutral-300 dark:border-neutral-700 bg-transparent text-neutral-900 dark:text-white focus:ring-2 focus:ring-black dark:focus:ring-white outline-none transition-all appearance-none"
                >
                  <option value="" disabled>Select a category</option>
                  {categories.map((cat) => (
                    <option key={cat.categoryId} value={cat.categoryId}>
                      {cat.categoryName}
                    </option>
                  ))}
                </select>
              </div>

              {isEditMode && (
                <div>
                  <label className="block text-sm font-medium text-neutral-900 dark:text-neutral-100 mb-2">
                    Slug <span className="text-neutral-400 font-normal">(Auto-generated if empty)</span>
                  </label>
                  <input
                    type="text"
                    name="slug"
                    value={formData.slug}
                    onChange={handleChange}
                    placeholder="vintage-leather-jacket"
                    className="w-full px-4 py-2.5 rounded-lg border border-neutral-300 dark:border-neutral-700 bg-transparent text-neutral-900 dark:text-white focus:ring-2 focus:ring-black dark:focus:ring-white outline-none transition-all font-mono text-sm"
                  />
                </div>
              )}
            </div>

            <div className="pt-6 border-t border-neutral-200 dark:border-neutral-800 flex justify-end">
              <button
                type="submit"
                disabled={isSaving}
                className="inline-flex items-center gap-2 px-6 py-2.5 bg-black text-white dark:bg-white dark:text-black rounded-lg text-sm font-medium hover:bg-neutral-800 dark:hover:bg-neutral-200 transition-colors disabled:opacity-50 min-w-[140px] justify-center"
              >
                {isSaving ? (
                  <Loader2 className="w-4 h-4 animate-spin" />
                ) : (
                  <Save className="w-4 h-4" />
                )}
                {isSaving ? "Saving..." : "Save Product"}
              </button>
            </div>
          </form>
        </div>

        {/* Sidebar / Images */}
        <div className="lg:col-span-1">
          {isEditMode ? (
            <div className="bg-white dark:bg-neutral-900 border border-neutral-200 dark:border-neutral-800 rounded-xl p-6 shadow-sm">
               <h2 className="text-lg font-semibold text-neutral-900 dark:text-white mb-4">
                  Product Images
               </h2>
               <p className="text-sm text-neutral-500 mb-6">
                 Image management is available in the dedicated images module.
               </p>
               {/* Image Manager Component */}
               <AdminProductImages productId={Number(id)} />
            </div>
          ) : (
            <div className="bg-neutral-50 dark:bg-neutral-900/50 border border-neutral-200 dark:border-neutral-800 rounded-xl p-6">
               <h2 className="text-sm font-semibold text-neutral-900 dark:text-white mb-2">
                  Images
               </h2>
               <p className="text-sm text-neutral-500">
                 You can add images after creating the product.
               </p>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default AdminProductForm;
