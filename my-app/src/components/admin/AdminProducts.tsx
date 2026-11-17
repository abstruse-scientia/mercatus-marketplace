import { useState, useEffect } from "react";
import { Plus, Search, Pencil, Power, PowerOff, Image as ImageIcon } from "lucide-react";
import { Link } from "react-router-dom";
import { adminApi } from "../../api/admin.api";
import type { AdminProduct } from "../../types";
import toast from "react-hot-toast";

const AdminProducts = () => {
  const [products, setProducts] = useState<AdminProduct[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [searchQuery, setSearchQuery] = useState("");
  
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

  const filteredProducts = products.filter((product) => {
    if (!searchQuery.trim()) return true;
    const q = searchQuery.toLowerCase().trim();
    return (
      product.name.toLowerCase().includes(q) ||
      (product.sku && product.sku.toLowerCase().includes(q)) ||
      (product.categoryName && product.categoryName.toLowerCase().includes(q))
    );
  });

  return (
    <div className="flex flex-col gap-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
        <div>
          <h1 className="text-2xl font-bold text-foreground">Products</h1>
          <p className="text-sm text-muted-foreground mt-1">
            Manage your store's inventory and product details.
          </p>
        </div>
        <Link
          to="/admin/products/new"
          className="inline-flex items-center gap-2 px-4 py-2.5 bg-primary text-primary-foreground rounded-lg text-sm font-medium hover:opacity-90 transition-opacity"
        >
          <Plus className="w-4 h-4" />
          Create Product
        </Link>
      </div>

      {/* Table Container */}
      <div className="bg-card text-card-foreground border border-border rounded-xl overflow-hidden shadow-sm">
        
        {/* Table Toolbar */}
        <div className="p-4 border-b border-border flex items-center justify-between">
          <div className="relative w-full max-w-sm">
            <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-muted-foreground" />
            <input
              type="text"
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              placeholder="Search products by name, SKU, or category..."
              className="w-full pl-9 pr-4 py-2 rounded-lg border border-border bg-background text-foreground text-sm focus:ring-2 focus:ring-ring focus:border-transparent outline-none placeholder:text-muted-foreground"
            />
          </div>
          <div className="text-sm text-muted-foreground">
            Total: {searchQuery.trim() ? `${filteredProducts.length} of ${totalElements}` : `${totalElements}`} items
          </div>
        </div>

        {/* Table */}
        <div className="overflow-x-auto">
          <table className="w-full text-left border-collapse">
            <thead>
              <tr className="bg-muted/40 border-b border-border">
                <th className="px-6 py-4 text-xs font-semibold text-muted-foreground uppercase tracking-wider">Product</th>
                <th className="px-6 py-4 text-xs font-semibold text-muted-foreground uppercase tracking-wider">SKU</th>
                <th className="px-6 py-4 text-xs font-semibold text-muted-foreground uppercase tracking-wider">Category</th>
                <th className="px-6 py-4 text-xs font-semibold text-muted-foreground uppercase tracking-wider">Price</th>
                <th className="px-6 py-4 text-xs font-semibold text-muted-foreground uppercase tracking-wider">Status</th>
                <th className="px-6 py-4 text-xs font-semibold text-muted-foreground uppercase tracking-wider text-right">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-border">
              {isLoading ? (
                <tr>
                  <td colSpan={6} className="px-6 py-8 text-center text-muted-foreground">
                    Loading products...
                  </td>
                </tr>
              ) : filteredProducts.length === 0 ? (
                <tr>
                  <td colSpan={6} className="px-6 py-8 text-center text-muted-foreground">
                    {searchQuery.trim()
                      ? `No products match "${searchQuery}".`
                      : "No products found. Create one to get started!"}
                  </td>
                </tr>
              ) : (
                filteredProducts.map((product) => (
                  <tr key={product.productId} className="hover:bg-accent/50 transition-colors">
                    <td className="px-6 py-4">
                      <div className="flex items-center gap-4">
                        {product.primaryImageUrl ? (
                          <img
                            src={product.primaryImageUrl}
                            alt={product.name}
                            className="w-10 h-10 rounded-md object-cover border border-border bg-card"
                          />
                        ) : (
                          <div className="w-10 h-10 rounded-md border border-dashed border-border flex items-center justify-center bg-muted">
                            <ImageIcon className="w-4 h-4 text-muted-foreground" />
                          </div>
                        )}
                        <span className="font-medium text-foreground line-clamp-1 max-w-[200px]">
                          {product.name}
                        </span>
                      </div>
                    </td>
                    <td className="px-6 py-4 text-sm text-muted-foreground font-mono">
                      {product.sku}
                    </td>
                    <td className="px-6 py-4 text-sm text-muted-foreground">
                      {product.categoryName}
                    </td>
                    <td className="px-6 py-4 text-sm font-medium text-foreground">
                      ${product.price.toFixed(2)}
                    </td>
                    <td className="px-6 py-4">
                      <span className={`inline-flex items-center px-2.5 py-1 rounded-full text-xs font-medium border ${
                        product.isActive 
                          ? 'bg-emerald-50 text-emerald-700 border-emerald-200 dark:bg-emerald-500/10 dark:text-emerald-400 dark:border-emerald-500/20' 
                          : 'bg-muted text-muted-foreground border-border'
                      }`}>
                        {product.isActive ? 'Active' : 'Inactive'}
                      </span>
                    </td>
                    <td className="px-6 py-4 text-right">
                      <div className="flex items-center justify-end gap-2">
                        <Link
                          to={`/admin/products/${product.productId}`}
                          className="p-2 text-muted-foreground hover:bg-accent hover:text-foreground rounded-lg transition-colors"
                          title="Edit Product"
                        >
                          <Pencil className="w-4 h-4" />
                        </Link>
                        <button
                          onClick={() => handleToggleStatus(product)}
                          className={`p-2 rounded-lg transition-colors ${
                            product.isActive 
                              ? 'text-destructive hover:bg-destructive/10' 
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
          <div className="p-4 border-t border-border flex items-center justify-between">
            <span className="text-sm text-muted-foreground">
              Showing page {page + 1} of {totalPages}
            </span>
            <div className="flex items-center gap-2">
              <button
                onClick={() => setPage((p) => Math.max(0, p - 1))}
                disabled={page === 0}
                className="px-3 py-1.5 text-sm border border-border rounded-md hover:bg-accent disabled:opacity-50 disabled:cursor-not-allowed transition-colors text-foreground"
              >
                Previous
              </button>
              <button
                onClick={() => setPage((p) => Math.min(totalPages - 1, p + 1))}
                disabled={page === totalPages - 1}
                className="px-3 py-1.5 text-sm border border-border rounded-md hover:bg-accent disabled:opacity-50 disabled:cursor-not-allowed transition-colors text-foreground"
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
