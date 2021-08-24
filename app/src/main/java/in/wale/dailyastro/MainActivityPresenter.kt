package `in`.wale.dailyastro

class MainActivityPresenter(val view: View) {

    private val repository = Repository()

    interface View {
        fun showProgress()

        fun showContentViews()
    }
}