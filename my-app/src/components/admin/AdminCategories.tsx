import { useState, useEffect } from "react";
import { Plus, Pencil, Trash2, Save, X, Loader2 } from "lucide-react";
import { adminApi } from "../../api/admin.api";
import type { Category } from "../../types";
import toast from "react-hot-toast";

const AdminCategories = () => {
  const [categories, setCategories] = useState<Category[]>([]);
  const [isLoading, setIsLoading] = useState(true);

  // Pagination
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);
  const [totalElements, setTotalElements] = useState(0);
  const pageSize = 10;

  // Inline Edit / Create State
  const [editingId, setEditingId] = useState<number | null>(null);
  const [editName, setEditName] = useState("");
  const [editSlug, setEditSlug] = useState("");
  const [isSaving, setIsSaving] = useState(false);

  const [isCreating, setIsCreating] = useState(false);
  const [newName, setNewName] = useState("");

  const fetchCategories = async () => {
    try {
      setIsLoading(true);
      const data = await adminApi.listCategories(page, pageSize);
      setCategories(data.content);
      setTotalPages(data.totalPages);
      setTotalElements(data.totalElements);
    } catch (error) {
      toast.error("Failed to load categories");
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    fetchCategories();
  }, [page]);

  const handleCreate = async () => {
    if (!newName.trim()) return;
    try {
      setIsSaving(true);
      await adminApi.createCategory({ name: newName });
      toast.success("Category created");
      setNewName("");
      setIsCreating(false);
      fetchCategories();
    } catch (error) {
      toast.error("Failed to create category");
    } finally {
      setIsSaving(false);
    }
  };

  const handleUpdate = async (id: number) => {
    if (!editName.trim()) return;
    try {
      setIsSaving(true);
      await adminApi.updateCategory(id, { categoryName: editName, slug: editSlug });
      toast.success("Category updated");
      setEditingId(null);
      fetchCategories();
    } catch (error) {
      toast.error("Failed to update category");
    } finally {
      setIsSaving(false);
    }
  };

  const handleDelete = async (id: number) => {
    if (!window.confirm("Are you sure? This might affect products in this category.")) return;
    try {
      await adminApi.deleteCategory(id);
      toast.success("Category deleted");
      fetchCategories();
    } catch (error) {
      toast.error("Failed to delete category. It might be in use.");
    }
  };

  const startEdit = (cat: Category) => {
    setEditingId(cat.categoryId);
    setEditName(cat.categoryName);
    setEditSlug(cat.slug);
    setIsCreating(false);
  };

  const cancelEdit = () => {
    setEditingId(null);
    setEditName("");
    setEditSlug("");
  };

  return (
    <div className="flex flex-col gap-6 max-w-4xl">
      {/* Header */}
      <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
        <div>
          <h1 className="text-2xl font-bold text-neutral-900 dark:text-white">Categories</h1>
          <p className="text-sm text-neutral-600 dark:text-neutral-400 mt-1">
            Manage your product categories.
          </p>
        </div>
        <button
          onClick={() => { setIsCreating(true); cancelEdit(); }}
          disabled={isCreating}
          className="inline-flex items-center gap-2 px-4 py-2.5 bg-black text-white dark:bg-white dark:text-black rounded-lg text-sm font-medium hover:bg-neutral-800 dark:hover:bg-neutral-200 transition-colors disabled:opacity-50"
        >
          <Plus className="w-4 h-4" />
          Add Category
        </button>
      </div>

      <div className="bg-white dark:bg-neutral-900 border border-neutral-200 dark:border-neutral-800 rounded-xl overflow-hidden shadow-sm">
        <div className="p-4 border-b border-neutral-200 dark:border-neutral-800 flex items-center justify-between">
          <div className="text-sm font-medium text-neutral-900 dark:text-white">
            All Categories
          </div>
          <div className="text-sm text-neutral-500">
            Total: {totalElements} items
          </div>
        </div>

        <div className="overflow-x-auto">
          <table className="w-full text-left border-collapse">
            <thead>
              <tr className="bg-neutral-50 dark:bg-neutral-900/50 border-b border-neutral-200 dark:border-neutral-800">
                <th className="px-6 py-4 text-xs font-semibold text-neutral-500 uppercase tracking-wider w-16">ID</th>
                <th className="px-6 py-4 text-xs font-semibold text-neutral-500 uppercase tracking-wider">Name</th>
                <th className="px-6 py-4 text-xs font-semibold text-neutral-500 uppercase tracking-wider">Slug</th>
                <th className="px-6 py-4 text-xs font-semibold text-neutral-500 uppercase tracking-wider text-right w-32">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-neutral-200 dark:divide-neutral-800">
              
              {/* Create Row Inline */}
              {isCreating && (
                <tr className="bg-blue-50/50 dark:bg-blue-900/10">
                  <td className="px-6 py-4 text-sm text-neutral-400">New</td>
                  <td className="px-6 py-4">
                    <input
                      autoFocus
                      type="text"
                      placeholder="Category Name"
                      value={newName}
                      onChange={(e) => setNewName(e.target.value)}
                      className="w-full px-3 py-1.5 rounded-md border border-neutral-300 dark:border-neutral-700 bg-white dark:bg-neutral-800 text-sm focus:ring-2 focus:ring-black outline-none"
                    />
                  </td>
                  <td className="px-6 py-4 text-sm text-neutral-400 italic">
                    Auto-generated
                  </td>
                  <td className="px-6 py-4 text-right">
                    <div className="flex items-center justify-end gap-2">
                      <button onClick={handleCreate} disabled={isSaving} className="p-1.5 text-emerald-600 hover:bg-emerald-50 rounded-md">
                        {isSaving ? <Loader2 className="w-4 h-4 animate-spin" /> : <Save className="w-4 h-4" />}
                      </button>
                      <button onClick={() => setIsCreating(false)} className="p-1.5 text-neutral-500 hover:bg-neutral-100 rounded-md">
                        <X className="w-4 h-4" />
                      </button>
                    </div>
                  </td>
                </tr>
              )}

              {isLoading ? (
                <tr>
                  <td colSpan={4} className="px-6 py-8 text-center text-neutral-500">
                    Loading categories...
                  </td>
                </tr>
              ) : categories.length === 0 && !isCreating ? (
                <tr>
                  <td colSpan={4} className="px-6 py-8 text-center text-neutral-500">
                    No categories found.
                  </td>
                </tr>
              ) : (
                categories.map((cat) => (
                  <tr key={cat.categoryId} className="hover:bg-neutral-50 dark:hover:bg-neutral-800/50 transition-colors">
                    <td className="px-6 py-4 text-sm text-neutral-500 font-mono">#{cat.categoryId}</td>
                    
                    {/* Editing Mode */}
                    {editingId === cat.categoryId ? (
                      <>
                        <td className="px-6 py-4">
                          <input
                            type="text"
                            value={editName}
                            onChange={(e) => setEditName(e.target.value)}
                            className="w-full px-3 py-1.5 rounded-md border border-neutral-300 dark:border-neutral-700 bg-white dark:bg-neutral-800 text-sm focus:ring-2 focus:ring-black outline-none"
                          />
                        </td>
                        <td className="px-6 py-4">
                          <input
                            type="text"
                            value={editSlug}
                            onChange={(e) => setEditSlug(e.target.value)}
                            className="w-full px-3 py-1.5 rounded-md border border-neutral-300 dark:border-neutral-700 bg-white dark:bg-neutral-800 text-sm focus:ring-2 focus:ring-black outline-none font-mono"
                          />
                        </td>
                        <td className="px-6 py-4 text-right">
                          <div className="flex items-center justify-end gap-2">
                            <button onClick={() => handleUpdate(cat.categoryId)} disabled={isSaving} className="p-1.5 text-emerald-600 hover:bg-emerald-50 dark:hover:bg-emerald-900/30 rounded-md">
                              {isSaving ? <Loader2 className="w-4 h-4 animate-spin" /> : <Save className="w-4 h-4" />}
                            </button>
                            <button onClick={cancelEdit} className="p-1.5 text-neutral-500 hover:bg-neutral-100 dark:hover:bg-neutral-800 rounded-md">
                              <X className="w-4 h-4" />
                            </button>
                          </div>
                        </td>
                      </>
                    ) : (
                      <>
                        <td className="px-6 py-4 font-medium text-neutral-900 dark:text-white">
                          {cat.categoryName}
                        </td>
                        <td className="px-6 py-4 text-sm text-neutral-500 font-mono">
                          {cat.slug}
                        </td>
                        <td className="px-6 py-4 text-right">
                          <div className="flex items-center justify-end gap-2">
                            <button onClick={() => startEdit(cat)} className="p-2 text-neutral-500 hover:bg-neutral-100 dark:hover:bg-neutral-800 hover:text-neutral-900 dark:hover:text-white rounded-lg transition-colors">
                              <Pencil className="w-4 h-4" />
                            </button>
                            <button onClick={() => handleDelete(cat.categoryId)} className="p-2 text-red-500 hover:bg-red-50 dark:hover:bg-red-900/20 rounded-lg transition-colors">
                              <Trash2 className="w-4 h-4" />
                            </button>
                          </div>
                        </td>
                      </>
                    )}
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>

        {/* Pagination */}
        {totalPages > 1 && (
          <div className="p-4 border-t border-neutral-200 dark:border-neutral-800 flex items-center justify-between">
            <span className="text-sm text-neutral-500">
              Showing page {page + 1} of {totalPages}
            </span>
            <div className="flex items-center gap-2">
              <button
                onClick={() => setPage((p) => Math.max(0, p - 1))}
                disabled={page === 0}
                className="px-3 py-1.5 text-sm border border-neutral-300 dark:border-neutral-700 rounded-md hover:bg-neutral-50 disabled:opacity-50 transition-colors"
              >
                Previous
              </button>
              <button
                onClick={() => setPage((p) => Math.min(totalPages - 1, p + 1))}
                disabled={page === totalPages - 1}
                className="px-3 py-1.5 text-sm border border-neutral-300 dark:border-neutral-700 rounded-md hover:bg-neutral-50 disabled:opacity-50 transition-colors"
              >
                Next
              </button>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default AdminCategories;
