#
# build file for java version rpm package
# (c) 2005 - 2008 Klaus Zerwes zero-sys.net
# 
# $Id: jsummer-java.spec,v 1.16 2008-03-06 11:01:15 zerwes Exp $
# 
# TODO:
#	dynamic version handling
#	fix requires - todo: swt
#	TODO: splitt file in header define and build
#	only theefine-part should differ from distro to distro
#

# norootforbuild 
# neededforbuild  ant javac


Summary: 	compute and check different message digest
Name: 		jsummer-java
Version: 	0.1.0.0
Release: 	1
License:	GPL
Group: 		Productivity/File utilities
Source: 	http://zero-sys.net/
Vendor: 	zero-sys.net
URL: 		http://zero-sys.net/
BuildRoot: 	%{_tmppath}/%{name}-%{version}-build
Provides: 	jsummer
Conflicts:	jsummer-native
BuildArch:	noarch

# start sysdependent
BuildRequires: 	ant java libswt3-gtk2
Requires:	jre libswt3-gtk2

%define prefix /usr
#%define binprefix /usr/X11 # SuSE
%define binprefix %{prefix}

# %define swtjar %{prefix}/share/java/swt-gtk-3.3.jar
# this is a uggly hack for suse's broken libswt
%define swtjar %{prefix}/share/java/swt-gtk-3.3.jar:/usr/lib/eclipse/swt.jar:/usr/share/eclipse/plugins/org.eclipse.swt.gtk.linux.x86_3.3.0.v3346.jar
# end sysdep

%description
A Tool to compute and check 
MD5, MD4, MD2, SHA-160, SHA-256, SHA-384, SHA-512, 
RIPEMD128, RIPEMD160, WHIRLPOOL, TIGER, HAVAL 
message digest.
Console- and GUI-version

Author:
(c) 2005 - 2008 Klaus Zerwes zero-sys.net


%prep
rm -rf $RPM_BUILD_DIR/JSummer-%{version}
zcat $RPM_SOURCE_DIR/JSummer-%{version}-src.tar.gz | tar -xf -


%build
cd JSummer-%{version}
cd build

ant -Dpackage.swt.jar=%{swtjar} \
    -Dpackage.swtsharedlibpath=/usr/%{_lib} \
    -Dpackage.installdir.jar=/usr/share/java \
    -Dpackage.swt.location=/usr/%{_lib} \
    jar starterscript-console starterscript-gui


%install
pwd
rm -rf $RPM_BUILD_ROOT
mkdir -p $RPM_BUILD_ROOT%{binprefix}/bin
mkdir -p $RPM_BUILD_ROOT%{prefix}/share
mkdir -p $RPM_BUILD_ROOT%{prefix}/share/pixmaps
mkdir -p $RPM_BUILD_ROOT%{prefix}/share/java
mkdir -p $RPM_BUILD_ROOT%{prefix}/share/applications
mkdir -p $RPM_BUILD_ROOT%{prefix}/share/man/man1
mkdir -p $RPM_BUILD_ROOT%{prefix}/share/mime-info
mkdir -p $RPM_BUILD_ROOT%{prefix}/share/mime/packages
mkdir -p $RPM_BUILD_ROOT%{prefix}/share/mimelnk/application
mkdir -p $RPM_BUILD_ROOT%{prefix}/share/apps/konqueror/servicemenus
mkdir -p $RPM_BUILD_ROOT%{prefix}/share/application-registry

cd JSummer-%{version}
#pwd
install -m 644 debian/jsummer.1 $RPM_BUILD_ROOT%{prefix}/share/man/man1/jsummer.1
install -m 644 icon/JSummer.xpm $RPM_BUILD_ROOT%{prefix}/share/pixmaps
install -m 644 build/out/JSummer.jar $RPM_BUILD_ROOT%{prefix}/share/java
install -m 644 build/out/JSummerX.jar $RPM_BUILD_ROOT%{prefix}/share/java
install -m 644 rpm/jsummer.desktop $RPM_BUILD_ROOT%{prefix}/share/applications
install -m 644 debian/JSummerX.desktop $RPM_BUILD_ROOT%{prefix}/share/applications
install -m 644 debian/jsummer.mime $RPM_BUILD_ROOT%{prefix}/share/mime-info
install -m 644 debian/jsummer.keys $RPM_BUILD_ROOT%{prefix}/share/mime-info
install -m 644 debian/jsummer.xml $RPM_BUILD_ROOT%{prefix}/share/mime/packages
install -m 644 debian/digest-*.desktop $RPM_BUILD_ROOT%{prefix}/share/mimelnk/application
install -m 644 debian/allfiles.desktop $RPM_BUILD_ROOT%{prefix}/share/apps/konqueror/servicemenus
install -m 644 debian/jsummer.applications $RPM_BUILD_ROOT%{prefix}/share/application-registry
install -m 755 build/out/JSummer.sh $RPM_BUILD_ROOT%{binprefix}/bin/
install -m 755 build/out/JSummerX.sh $RPM_BUILD_ROOT%{binprefix}/bin/
ln -sv %{binprefix}/bin/JSummer.sh $RPM_BUILD_ROOT%{binprefix}/bin/JSummer
ln -sv %{binprefix}/bin/JSummerX.sh $RPM_BUILD_ROOT%{binprefix}/bin/JSummerX
ln -sv %{binprefix}/bin/JSummer.sh $RPM_BUILD_ROOT%{binprefix}/bin/jsummer
ln -sv %{binprefix}/bin/JSummerX.sh $RPM_BUILD_ROOT%{binprefix}/bin/jsummerx



%clean
rm -rf $RPM_BUILD_ROOT


%post
if [ -x "`which update-menus 2>/dev/null`" ]; then
	update-menus
fi
if [ -x "`which update-mime 2>/dev/null`" ]; then
	update-mime
fi
if [ -x "`which update-mime-database 2>/dev/null`" ]; then
	update-mime-database /usr/share/mime
fi
if [ -x "`which update-desktop-database 2>/dev/null`" ]; then
    update-desktop-database
fi
if [ -d /opt/kde3/share/mimelnk/application ]; then
	for i in digest-md5.desktop digest-md4.desktop digest-md2.desktop digest-sha160.desktop digest-sha256.desktop digest-sha384.desktop digest-sha512.desktop digest-ripemd128.desktop digest-ripemd160.desktop digest-whirlpool.desktop digest-tiger.desktop digest-haval.desktop; do
		[ -f /opt/kde3/share/mimelnk/application/$i ] || \
			ln -s %{prefix}/share/mimelnk/application/$i /opt/kde3/share/mimelnk/application/
	done
fi
if [ -d /opt/kde3/share/apps/konqueror/servicemenus ]; then
	for i in allfiles.desktop; do
		[ -f /opt/kde3/share/apps/konqueror/servicemenus/$i ] || \
			ln -s %{prefix}/share/apps/konqueror/servicemenus/$i /opt/kde3/share/apps/konqueror/servicemenus/
	done
fi
if [ -d /opt/gnome/share/application-registry ]; then
	for i in jsummer.applications; do
		[ -f /opt/gnome/share/application-registry/$i ] || \
			ln -s %{prefix}/share/application-registry/$i /opt/gnome/share/application-registry/
	done
fi
if [ -d /opt/gnome/share/mime-info ]; then
	for i in jsummer.mime jsummer.keys; do
		[ -f /opt/gnome/share/mime-info/$i ] || \
			ln -s %{prefix}/share/mime-info/$i /opt/gnome/share/mime-info/
	done
fi

%postun
if [ -x "`which update-menus 2>/dev/null`" ]; then
	update-menus
fi
if [ -x "`which update-mime 2>/dev/null`" ]; then
	update-mime
fi
if [ -x "`which update-mime-database 2>/dev/null`" ]; then
	update-mime-database /usr/share/mime
fi
if [ -x "`which update-desktop-database 2>/dev/null`" ]; then
    update-desktop-database
fi
if [ -d /opt/kde3/share/mimelnk/application ]; then
	for i in digest-md5.desktop digest-md4.desktop digest-md2.desktop digest-sha160.desktop digest-sha256.desktop digest-sha384.desktop digest-sha512.desktop digest-ripemd128.desktop digest-ripemd160.desktop digest-whirlpool.desktop digest-tiger.desktop digest-haval.desktop; do
		[ -L /opt/kde3/share/mimelnk/application/$i ] && \
			rm /opt/kde3/share/mimelnk/application/$i
	done
fi
if [ -d /opt/kde3/share/apps/konqueror/servicemenus ]; then
	for i in allfiles.desktop; do
		[ -L /opt/kde3/share/apps/konqueror/servicemenus/$i ] && \
			rm /opt/kde3/share/apps/konqueror/servicemenus/$i
	done
fi
if [ -d /opt/gnome/share/application-registry ]; then
	for i in jsummer.applications; do
		[ -L /opt/gnome/share/application-registry/$i ] && \
			rm /opt/gnome/share/application-registry/$i
	done
fi
if [ -d /opt/gnome/share/mime-info ]; then
	for i in jsummer.mime jsummer.keys; do
		[ -L /opt/gnome/share/mime-info/$i ] || \
			rm /opt/gnome/share/mime-info/$i
	done
fi


%files
%defattr(-,root,root)
%doc JSummer-%{version}/License.html JSummer-%{version}/Changelog
%{binprefix}/bin/jsummer
%{binprefix}/bin/jsummerx
%{binprefix}/bin/JSummer
%{binprefix}/bin/JSummerX
%{binprefix}/bin/JSummer.sh
%{binprefix}/bin/JSummerX.sh
%{prefix}/share/man/man1/jsummer.1.gz
%{prefix}/share/java/JSummer.jar
%{prefix}/share/java/JSummerX.jar
%{prefix}/share/pixmaps/JSummer.xpm
%{prefix}/share/applications/jsummer.desktop
%{prefix}/share/applications/JSummerX.desktop
%{prefix}/share/mime-info/jsummer.mime
%{prefix}/share/mime-info/jsummer.keys
%{prefix}/share/mime/packages/jsummer.xml
%{prefix}/share/mimelnk/application/digest-*.desktop
%{prefix}/share/apps/konqueror/servicemenus/allfiles.desktop
%{prefix}/share/application-registry/jsummer.applications
