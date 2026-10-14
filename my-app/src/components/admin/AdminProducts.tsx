import React, { useState, useEffect } from "react";
import { Plus, Search, Pencil, Power, PowerOff, Image as ImageIcon } from "lucide-react";
import { Link } from "react-router-dom";
import { adminApi } from "../../api/admin.api";
import type { AdminProduct } from "../../types";
import toast from "react-hot-toast";

const AdminProducts: React.FC = () => {
  const [products, setProducts] = useState<AdminProduct[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  
  // Pagination
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);
  const [totalElements, setTotalElements] = useState(0);
  const pageSize = 10;

  const fetchProducts = async () => {
    try {
      setIsLoading(true);
      const data = await adminApi.listProducts(page, pageSize);
      setProducts(data.content);
      setTotalPages(data.totalPages);
      setTotalElements(data.totalElements);
    } catch (error) {
      toast.error("Failed to load products");
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    fetchProducts();
  }, [page]);

  const handleToggleStatus = async (product: AdminProduct) => {
    try {
      if (product.isActive) {
        await adminApi.deactivateProduct(product.productId);
        toast.success(`Deactivated ${product.name}`);
      } else {
        await adminApi.reactivateProduct(product.productId);
        toast.success(`Reactivated ${product.name}`);
      }
      fetchProducts(); // Refresh list
    } catch (error) {
      toast.error(`Failed to change status for ${product.name}`);
    }
  };

  return (
    <div className="flex flex-col gap-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
        <div>
          <h1 className="text-2xl font-bold text-neutral-900 dark:text-white">Products</h1>
          <p className="text-sm text-neutral-600 dark:text-neutral-400 mt-1">
            Manage your store's inventory and product details.
          </p>
        </div>
        <Link
          to="/admin/products/new"
          className="inline-flex items-center gap-2 px-4 py-2.5 bg-black text-white dark:bg-white dark:text-black rounded-lg text-sm font-medium hover:bg-neutral-800 dark:hover:bg-neutral-200 transition-colors"
        >
          <Plus className="w-4 h-4" />
          Create Product
        </Link>
      </div>

      {/* Table Container */}
      <div className="bg-white dark:bg-neutral-900 border border-neutral-200 dark:border-neutral-800 rounded-xl overflow-hidden shadow-sm">
        
        {/* Table Toolbar */}
        <div className="p-4 border-b border-neutral-200 dark:border-neutral-800 flex items-center justify-between">
          <div className="relative w-full max-w-sm">
            <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-neutral-400" />
            <input
              type="text"
              placeholder="Search products..."
              className="w-full pl-9 pr-4 py-2 rounded-lg border border-neutral-300 dark:border-neutral-700 bg-transparent text-sm focus:ring-2 focus:ring-black dark:focus:ring-white focus:border-transparent outline-none"
            />
          </div>
          <div className="text-sm text-neutral-500">
            Total: {totalElements} items
          </div>
        </div>

        {/* Table */}
        <div className="overflow-x-auto">
          <table className="w-full text-left border-collapse">
            <thead>
              <tr className="bg-neutral-50 dark:bg-neutral-900/50 border-b border-neutral-200 dark:border-neutral-800">
                <th className="px-6 py-4 text-xs font-semibold text-neutral-500 uppercase tracking-wider">Product</th>
                <th className="px-6 py-4 text-xs font-semibold text-neutral-500 uppercase tracking-wider">SKU</th>
                <th className="px-6 py-4 text-xs font-semibold text-neutral-500 uppercase tracking-wider">Category</th>
                <th className="px-6 py-4 text-xs font-semibold text-neutral-500 uppercase tracking-wider">Price</th>
                <th className="px-6 py-4 text-xs font-semibold text-neutral-500 uppercase tracking-wider">Status</th>
                <th className="px-6 py-4 text-xs font-semibold text-neutral-500 uppercase tracking-wider text-right">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-neutral-200 dark:divide-neutral-800">
              {isLoading ? (
                <tr>
                  <td colSpan={6} className="px-6 py-8 text-center text-neutral-500">
                    Loading products...
                  </td>
                </tr>
              ) : products.length === 0 ? (
                <tr>
                  <td colSpan={6} className="px-6 py-8 text-center text-neutral-500">
                    No products found. Create one to get started!
                  </td>
                </tr>
              ) : (
                products.map((product) => (
                  <tr key={product.productId} className="hover:bg-neutral-50 dark:hover:bg-neutral-800/50 transition-colors">
                    <td className="px-6 py-4">
                      <div className="flex items-center gap-4">
                        {product.primaryImageUrl ? (
                          <img
                            src={product.primaryImageUrl}
                            alt={product.name}
                            className="w-10 h-10 rounded-md object-cover border border-neutral-200 dark:border-neutral-700 bg-white"
                          />
                        ) : (
                          <div className="w-10 h-10 rounded-md border border-dashed border-neutral-300 dark:border-neutral-700 flex items-center justify-center bg-neutral-50 dark:bg-neutral-800">
                            <ImageIcon className="w-4 h-4 text-neutral-400" />
                          </div>
                        )}
                        <span className="font-medium text-neutral-900 dark:text-white line-clamp-1 max-w-[200px]">
                          {product.name}
                        </span>
                      </div>
                    </td>
                    <td className="px-6 py-4 text-sm text-neutral-600 dark:text-neutral-400 font-mono">
                      {product.sku}
                    </td>
                    <td className="px-6 py-4 text-sm text-neutral-600 dark:text-neutral-400">
                      {product.categoryName}
                    </td>
                    <td className="px-6 py-4 text-sm font-medium text-neutral-900 dark:text-white">
                      ${product.price.toFixed(2)}
                    </td>
                    <td className="px-6 py-4">
                      <span className={`inline-flex items-center px-2.5 py-1 rounded-full text-xs font-medium border ${
                        product.isActive 
                          ? 'bg-emerald-50 text-emerald-700 border-emerald-200 dark:bg-emerald-500/10 dark:text-emerald-400 dark:border-emerald-500/20' 
                          : 'bg-neutral-100 text-neutral-600 border-neutral-200 dark:bg-neutral-800 dark:text-neutral-400 dark:border-neutral-700'
                      }`}>
                        {product.isActive ? 'Active' : 'Inactive'}
                      </span>
                    </td>
                    <td className="px-6 py-4 text-right">
                      <div className="flex items-center justify-end gap-2">
                        <Link
                          to={`/admin/products/${product.productId}`}
                          className="p-2 text-neutral-500 hover:bg-neutral-100 hover:text-neutral-900 dark:hover:bg-neutral-800 dark:hover:text-white rounded-lg transition-colors"
                          title="Edit Product"
                        >
                          <Pencil className="w-4 h-4" />
                        </Link>
                        <button
                          onClick={() => handleToggleStatus(product)}
                          className={`p-2 rounded-lg transition-colors ${
                            product.isActive 
                              ? 'text-red-500 hover:bg-red-50 dark:hover:bg-red-500/10' 
                              : 'text-emerald-600 hover:bg-emerald-50 dark:hover:bg-emerald-500/10'
                          }`}
                          title={product.isActive ? "Deactivate Product" : "Reactivate Product"}
                        >
                          {product.isActive ? <PowerOff className="w-4 h-4" /> : <Power className="w-4 h-4" />}
                        </button>
                      </div>
                    </td>
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
                className="px-3 py-1.5 text-sm border border-neutral-300 dark:border-neutral-700 rounded-md hover:bg-neutral-50 dark:hover:bg-neutral-800 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
              >
                Previous
              </button>
              <button
                onClick={() => setPage((p) => Math.min(totalPages - 1, p + 1))}
                disabled={page === totalPages - 1}
                className="px-3 py-1.5 text-sm border border-neutral-300 dark:border-neutral-700 rounded-md hover:bg-neutral-50 dark:hover:bg-neutral-800 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
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

export default AdminProducts;
