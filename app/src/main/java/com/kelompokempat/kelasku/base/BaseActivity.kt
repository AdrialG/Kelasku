package com.kelompokempat.kelasku.base

import androidx.databinding.ViewDataBinding
import com.crocodic.core.base.activity.CoreActivity
import com.crocodic.core.base.viewmodel.CoreViewModel

open class BaseActivity<VB: ViewDataBinding, VM: CoreViewModel>(layoutRes:  Int): CoreActivity<VB, VM>(layoutRes) {

}