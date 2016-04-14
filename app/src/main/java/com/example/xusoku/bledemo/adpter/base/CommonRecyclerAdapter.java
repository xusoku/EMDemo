package com.example.xusoku.bledemo.adpter.base;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;

import java.util.List;

public abstract class CommonRecyclerAdapter<Model> extends
		RecyclerView.Adapter<CommonRecyclerAdapter.BaseViewHolder> {

	protected LayoutInflater mInflater;
	protected Context mContext;
	protected List<Model> mDatas;
	protected final int mItemLayoutId;
	/**
	 * item 类型
	 */
	public final static int TYPE_NORMAL = 0;
	public final static int TYPE_HEADER = 1;// 头部--支持头部增加一个headerView
	public final static int TYPE_FOOTER = 2;// 底部--往往是loading_more

	public interface OnItemClickLitener {
		void onItemClick(View itemView, int position);

		void onItemLongClick(View itemView, int position);
	}

	private OnItemClickLitener mOnItemClickLitener;
	private View mHeaderView;
	private View mFooterView;

	public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener) {
		this.mOnItemClickLitener = mOnItemClickLitener;
	}

	public CommonRecyclerAdapter(Context context, List<Model> mDatas,
			int itemLayoutId) {
		this.mContext = context;
		this.mInflater = LayoutInflater.from(mContext);
		this.mDatas = mDatas;
		this.mItemLayoutId = itemLayoutId;

	}

	public void addHeaderView(View headerView) {
		mHeaderView = headerView;
		notifyItemInserted(0);
	}

	public View getHeaderView() {
		return mHeaderView;
	}

	public void addFooterView(View footerView) {
		mFooterView = footerView;
		notifyDataSetChanged();
	}

	public View getFooterView() {
		return mFooterView;
	}

	@Override
	public int getItemCount() {
		int size = mDatas.size();
		if (mFooterView != null) {
			size++;
		}
		if (mHeaderView != null) {
			size++;
		}
		return size;
	}

	
	public abstract void convert(BaseViewHolder holder, Model itemData,
			int position);

	@Override
	public int getItemViewType(int position) {
		int headerPosition = 0;
		int footerPosition = getItemCount() - 1;
		if (position == headerPosition && mHeaderView != null)
			return TYPE_HEADER;
		if (footerPosition == position && mFooterView != null) {
			return TYPE_FOOTER;
		}

		return TYPE_NORMAL;
	}

	private int getRealPosition(int position) {

		return mHeaderView == null ? position : position - 1;
	}
	public Model getItemData(int position) {
		return mDatas.get(position);
	}

	/*
	 * 也可以重写该方法，根据不同的类型，来自定义不同的Holder，这里统一所以的类型，都是同一种Holder（每个item都有一个自己的Holder对象
	 * ）
	 */
	@Override
	public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

		if (viewType == TYPE_HEADER) {
            return new BaseViewHolder(mHeaderView);
        }
        if (viewType == TYPE_FOOTER) {
        	return new BaseViewHolder(mFooterView);
        } else { // type normal
        	return new BaseViewHolder(mInflater.inflate(
    				mItemLayoutId, parent, false));
        }
		

	}

	@Override
	public void onBindViewHolder(final BaseViewHolder holder, final int position) {

		int type = getItemViewType(position);
		if (type == TYPE_FOOTER || type == TYPE_HEADER) {
			return;
		}
		final int pos = getRealPosition(position);
		convert(holder, getItemData(pos), pos);

		// 如果设置了回调，则设置点击事件
		if (mOnItemClickLitener != null) {
			holder.itemView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {

					mOnItemClickLitener.onItemClick(holder.itemView, pos);
				}
			});

			holder.itemView.setOnLongClickListener(new OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {

					mOnItemClickLitener.onItemLongClick(holder.itemView,
							pos);
					return false;
				}
			});
		}
	}

	@Override
	public void onAttachedToRecyclerView(RecyclerView recyclerView) {
	    super.onAttachedToRecyclerView(recyclerView);
	    RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
	    if(manager instanceof GridLayoutManager) {
	        final GridLayoutManager gridManager = ((GridLayoutManager) manager);
	        gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
	            @Override
	            public int getSpanSize(int position) {
	            	int type = getItemViewType(position);
	            	if (type==TYPE_HEADER||type==TYPE_FOOTER) {
						return gridManager.getSpanCount();
					}
	                return 1;
	            }
	        });
	    }
	}
	
	@Override
	public void onViewAttachedToWindow(BaseViewHolder holder) {
	    super.onViewAttachedToWindow(holder);
	    ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
	    if(lp != null
	            && lp instanceof StaggeredGridLayoutManager.LayoutParams) {
	            StaggeredGridLayoutManager.LayoutParams params = (StaggeredGridLayoutManager.LayoutParams) lp;
	            int type = getItemViewType(holder.getPosition());
            	if (type==TYPE_HEADER||type==TYPE_FOOTER) {
            		params.setFullSpan(true);
				}
	          
	    }
	}
	/**
	 * 插入一个数据
	 */
	public void insert(int position, Model model) {
		if (position < 0) {
			position = 0;
		}

		if (position > mDatas.size()) {
			position = mDatas.size() - 1;
		}
		mDatas.add(position, model);
		notifyItemInserted(position);
	}

	/**
	 * 加入一个数据到最后
	 * 
	 * @param model
	 */
	public void append(Model model) {
		mDatas.add(model);
		notifyItemInserted(mDatas.size() - 1);
	}

	/**
	 * 移除一个数据
	 * 
	 * @param position
	 */
	public void remove(int position) {
		mDatas.remove(position);
		notifyItemRemoved(position);
	}

	/**
	 * 清除所有的数据
	 */
	public void clear() {
		mDatas.clear();
		notifyDataSetChanged();
	}

	public static class BaseViewHolder extends RecyclerView.ViewHolder {

		private SparseArray<View> mViews;

		public BaseViewHolder(View itemView) {
			super(itemView);
			this.mViews = new SparseArray<View>();
		}

		public <T extends View> T getView(int viewId) {
			View view = mViews.get(viewId);
			if (view == null) {
				view = itemView.findViewById(viewId);
				mViews.put(viewId, view);
			}
			return (T) view;
		}
	}

}
