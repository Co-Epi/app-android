CURBRANCH=`git branch | grep \\* | sed s/*//`
read -p "REBASE $CURBRANCH? (press CTRL+C to stop)" 
git rebase origin master
read -p "Continue with merge $1?" 
git merge $1

